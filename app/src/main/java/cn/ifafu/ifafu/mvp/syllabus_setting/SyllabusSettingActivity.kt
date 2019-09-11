package cn.ifafu.ifafu.mvp.syllabus_setting

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.util.DensityUtils
import cn.ifafu.ifafu.util.Glide4Engine
import cn.ifafu.ifafu.view.adapter.syllabus_setting.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.gyf.immersionbar.ImmersionBar
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.activity_syllabus_setting.*
import me.drakeet.multitype.MultiTypeAdapter
import java.util.*

class SyllabusSettingActivity : BaseActivity<SyllabusSettingContract.Presenter>(), SyllabusSettingContract.View {

    private lateinit var setting: SyllabusSetting

    private val REQUEST_CODE_CHOOSE = 23

    private val colors = intArrayOf(
            Color.BLACK, Color.DKGRAY, Color.GRAY, Color.LTGRAY,
            Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
            Color.YELLOW, Color.CYAN)

    private val mPicturePicker by lazy {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .gridExpectedSize(DensityUtils.dp2px(context, 120F).toInt())
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(Glide4Engine())
    }

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_syllabus_setting
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_syllabus_setting)
                .statusBarDarkFont(true)
                .statusBarColor("#FFFFFF")
                .init()

        mPresenter = SyllabusSettingPresenter(this)

        tb_syllabus_setting.setNavigationOnClickListener { finish() }
    }

    override fun initRecycleView(setting: SyllabusSetting) {
        this.setting = setting
        val adapter = MultiTypeAdapter()
        adapter.register(SeekBarItem::class, SeekBarBinder())
        adapter.register(CheckBoxItem::class, CheckBoxBinder())
        adapter.register(TextViewItem::class, TextViewBinder())
        adapter.register(ColorItem::class, ColorBinder())
        adapter.items = listOf(
                SeekBarItem("一天课程的节数", setting.totalNode, "节", 8, 12) {
                    setting.totalNode = it
                },
                SeekBarItem("总共周数", setting.weekCnt, "周", 18, 24) {
                    setting.weekCnt = it
                },
                SeekBarItem("课程字体大小", setting.textSize, "sp", 8, 18) {
                    setting.textSize = it
                },
                CheckBoxItem("周日为每周第一天", "请根据学校情况设置", setting.firstDayOfWeek == Calendar.SUNDAY) {
                    if (it) {
                        setting.firstDayOfWeek = Calendar.SUNDAY
                    } else {
                        setting.firstDayOfWeek = Calendar.MONDAY
                    }
                },
                CheckBoxItem("显示水平分割线", "", setting.showHorizontalLine) {
                    setting.showHorizontalLine = it
                },
                CheckBoxItem("显示垂直分割线", "", setting.showVerticalLine) {
                    setting.showVerticalLine = it
                },
                CheckBoxItem("显示上课时间", "", setting.showBeginTimeText) {
                    setting.showBeginTimeText = it
                },
                CheckBoxItem("标题栏深色字体", "", setting.statusDartFont) {
                    setting.statusDartFont = it
                },
                TextViewItem("课表背景", "长按重置为默认背景", {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                    } else {
                        mPicturePicker.forResult(REQUEST_CODE_CHOOSE)
                    }
                }, { setting.background = null }),
                ColorItem("主题颜色", "按钮颜色，文本颜色（除课程文本）", setting.themeColor) { ivColor ->
                    MaterialDialog(this).show {
                        title(text = "请选择颜色")
                        colorChooser(colors = colors, initialSelection = setting.themeColor)
                        { _, color ->
                            val grad = ivColor.background as GradientDrawable
                            grad.setColor(color)
                            setting.themeColor = color
                        }
                        positiveButton(text = "确认")
                        negativeButton(text = "取消")
                    }
                }
        )
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        context.getDrawable(R.drawable.shape_divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        rv_syllabus_setting.addItemDecoration(dividerItemDecoration)
        rv_syllabus_setting.layoutManager = LinearLayoutManager(this)
        rv_syllabus_setting.adapter = adapter
    }

    override fun finish() {
        mPresenter.save(setting)
        super.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            setting.background = Matisse.obtainResult(data)[0].toString()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPicturePicker.forResult(REQUEST_CODE_CHOOSE)
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
