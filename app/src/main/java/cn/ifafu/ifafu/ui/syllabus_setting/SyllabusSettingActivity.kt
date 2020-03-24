package cn.ifafu.ifafu.ui.syllabus_setting

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.databinding.SyllabusSettingActivityBinding
import cn.ifafu.ifafu.util.DensityUtils
import cn.ifafu.ifafu.util.Glide4Engine
import cn.ifafu.ifafu.view.adapter.syllabus_setting.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.gyf.immersionbar.ImmersionBar
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.syllabus_setting_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.drakeet.multitype.MultiTypeAdapter

class SyllabusSettingActivity : BaseActivity<SyllabusSettingActivityBinding, SyllabusSettingViewModel>() {

    private val REQUEST_CODE_CHOOSE_ACTIVITY = 23
    private val REQUEST_CODE_PERMISSION = 24
    private val mPicturePicker by lazy {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(DensityUtils.dp2px(this, 120F))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(Glide4Engine())
    }
    private val mAdapter by lazy {
        MultiTypeAdapter().apply {
            register(SeekBarItem::class, SeekBarBinder())
            register(CheckBoxItem::class, CheckBoxBinder())
            register(TextViewItem::class, TextViewBinder())
            register(ColorItem::class, ColorBinder())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.syllabus_setting_activity
    }

    override fun getViewModel(): SyllabusSettingViewModel? {
        return VMProvider(this)[SyllabusSettingViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_syllabus_setting)
                .statusBarDarkFont(true)
                .statusBarColor("#FFFFFF")
                .init()
        tb_syllabus_setting.setNavigationOnClickListener { finish() }
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        this.getDrawable(R.drawable.shape_divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        rv_syllabus_setting.addItemDecoration(dividerItemDecoration)
        rv_syllabus_setting.layoutManager = LinearLayoutManager(this)
        rv_syllabus_setting.adapter = mAdapter
        mViewModel.setting.observe(this, Observer { setting ->
            mAdapter.items = listOf(
                    SeekBarItem("一天课程的节数", setting.totalNode, "节", 8, 12) {
                        setting.totalNode = it
                        mViewModel.save()
                        setResult(Activity.RESULT_OK)
                    },
                    SeekBarItem("课程字体大小", setting.textSize, "sp", 8, 18) {
                        setting.textSize = it
                        mViewModel.save()
                        setResult(Activity.RESULT_OK)
                    },
                    CheckBoxItem("显示水平分割线", "", setting.showHorizontalLine) {
                        setting.showHorizontalLine = it
                        mViewModel.save()
                        setResult(Activity.RESULT_OK)
                    },
                    CheckBoxItem("显示垂直分割线", "", setting.showVerticalLine) {
                        setting.showVerticalLine = it
                        mViewModel.save()
                        setResult(Activity.RESULT_OK)
                    },
                    CheckBoxItem("显示上课时间", "", setting.showBeginTimeText) {
                        setting.showBeginTimeText = it
                        mViewModel.save()
                        setResult(Activity.RESULT_OK)
                    },
                    CheckBoxItem("标题栏深色字体", "", setting.statusDartFont) {
                        setting.statusDartFont = it
                        mViewModel.save()
                        setResult(Activity.RESULT_OK)
                    },
                    TextViewItem("课表背景", "长按重置为默认背景", {
                        showPicturePicker()
                        setResult(Activity.RESULT_OK)
                    }, {
                        setting.background = ""
                        mViewModel.save()
                        GlobalScope.launch(Dispatchers.Main) {
                            showMessage("课表背景已重置")
                        }
                        setResult(Activity.RESULT_OK)
                    }),
                    ColorItem("主题颜色", "按钮颜色，文本颜色（除课程文本）", setting.themeColor) { ivColor ->
                        showColorPicker(setting, ivColor)
                        setResult(Activity.RESULT_OK)
                    },
                    TextViewItem("导出测试数据到剪切板", "", {
                        mViewModel.outputHtml()
                        setResult(Activity.RESULT_OK)
                    }, {})
            )
            mAdapter.notifyDataSetChanged()
        })
        mViewModel.init()
    }

    private fun showPicturePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        } else {
            mPicturePicker.forResult(REQUEST_CODE_CHOOSE_ACTIVITY)
        }
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
                mViewModel.save()
            }
            negativeButton(text = "取消")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CHOOSE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            mViewModel.setting.value?.background = Matisse.obtainResult(data)[0].toString()
            mViewModel.save()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPicturePicker.forResult(REQUEST_CODE_CHOOSE_ACTIVITY)
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
