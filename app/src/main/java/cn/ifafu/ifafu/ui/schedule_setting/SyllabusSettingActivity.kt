package cn.ifafu.ifafu.ui.schedule_setting

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.databinding.ActivityScheduleSettingBinding
import cn.ifafu.ifafu.ui.setting.SettingAdapter
import cn.ifafu.ifafu.ui.setting.SettingItem
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import kotlinx.android.synthetic.main.activity_schedule_setting.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File

class SyllabusSettingActivity : BaseActivity() {

    private val PICK = 1001
    private val CROP = 1002

    private lateinit var account: String

    private val mAdapter = SettingAdapter()

    private val mViewModel by viewModel<SyllabusSettingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        bind<ActivityScheduleSettingBinding>(R.layout.activity_schedule_setting)
        tb_syllabus_setting.setNavigationOnClickListener { finish() }
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        this.getDrawable(R.drawable.shape_divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        rv_syllabus_setting.addItemDecoration(dividerItemDecoration)
        rv_syllabus_setting.layoutManager = LinearLayoutManager(this)
        rv_syllabus_setting.adapter = mAdapter

        mViewModel.setting.observe(this, Observer { setting ->
            mAdapter.setNewInstance(listOf(
                    SettingItem.SeekBar("一天课程的节数", setting.totalNode, "节", 8, 12) {
                        setting.totalNode = it
                        mViewModel.save(setting)
                        setResult(Activity.RESULT_OK)
                    },
                    SettingItem.SeekBar("课程字体大小", setting.textSize, "sp", 8, 18) {
                        setting.textSize = it
                        mViewModel.save(setting)
                        setResult(Activity.RESULT_OK)
                    },
                    SettingItem.CheckBox("显示水平分割线", "", setting.showHorizontalLine) {
                        setting.showHorizontalLine = it
                        mViewModel.save(setting)
                        setResult(Activity.RESULT_OK)
                    },
                    SettingItem.CheckBox("显示垂直分割线", "", setting.showVerticalLine) {
                        setting.showVerticalLine = it
                        mViewModel.save(setting)
                        setResult(Activity.RESULT_OK)
                    },
                    SettingItem.CheckBox("显示上课时间", "", setting.showBeginTimeText) {
                        setting.showBeginTimeText = it
                        mViewModel.save(setting)
                        setResult(Activity.RESULT_OK)
                    },
                    SettingItem.CheckBox("标题栏深色字体", "", setting.statusDartFont) {
                        setting.statusDartFont = it
                        mViewModel.save(setting)
                        setResult(Activity.RESULT_OK)
                    },
                    SettingItem.Text("课表背景", "长按重置为默认背景", {
                        val intent = Intent(Intent.ACTION_PICK).apply {
                            type = "image/*"
                        }
                        Timber.d("height: ${window.decorView.height}, width: ${window.decorView.width}")
                        startActivityForResult(intent, PICK)
                    }, {
                        val file = File(getExternalFilesDir(account), "syllabus_bg.jpg")
                        if (file.exists()) {
                            file.delete()
                        }
                        setting.background = ""
                        mViewModel.save(setting)
                        GlobalScope.launch(Dispatchers.Main) {
                            toast("课表背景已重置")
                        }
                        setResult(Activity.RESULT_OK)
                    }),
                    SettingItem.Color("主题颜色", "按钮颜色，文本颜色（除课程文本）", setting.themeColor) { ivColor ->
                        showColorPicker(setting, ivColor)
                        setResult(Activity.RESULT_OK)
                    }
//                    SettingItem.Text("导出测试数据到剪切板", "", {
//                        mViewModel.outputHtml()
//                        setResult(Activity.RESULT_OK)
//                    })
            ).toMutableList())
        })
    }

    private fun crop(uri: Uri) {
        val intent = Intent("com.android.camera.action.CROP").apply {
            setDataAndType(uri, "image/*")
            putExtra("crop", "true")
            putExtra("aspectX", window.decorView.width);
            putExtra("aspectY", window.decorView.height);
            intent.putExtra("outputX", window.decorView.width); // 宽尺寸
            intent.putExtra("outputY", window.decorView.height); // 高尺寸
            intent.putExtra("scale", true); // 保持比例
            //临时方案，没想好别乱改，注意和课表背景加载的关系！！！
            val file = File(getExternalFilesDir(account), "syllabus_bg.jpg")
            putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
            putExtra("outputFormat", Bitmap.CompressFormat.JPEG)
        }
        startActivityForResult(intent, CROP)
    }

    private fun showColorPicker(setting: SyllabusSetting, ivColor: ImageView) {
        MaterialDialog(this).show {
            var selectColor = 0
            title(text = "请选择颜色")
            colorChooser(colors = intArrayOf(
                    Color.BLACK, Color.DKGRAY, Color.GRAY, Color.LTGRAY,
                    Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
                    Color.YELLOW, Color.CYAN), initialSelection = setting.themeColor)
            { _, color ->
                selectColor = color
            }
            positiveButton(text = "确认") {
                val grad = ivColor.background as GradientDrawable?
                grad?.setColor(selectColor)
                setting.themeColor = selectColor
                mViewModel.save(setting)
            }
            negativeButton(text = "取消")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { crop(it) }
        } else if (requestCode == CROP && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
