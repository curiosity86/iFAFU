package cn.ifafu.ifafu.mvp.syllabus_setting

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.entity.SyllabusSetting
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

class SyllabusSettingActivity : BaseActivity<SyllabusSettingContract.Presenter>(), SyllabusSettingContract.View {

    private val REQUEST_CODE_CHOOSE_ACTIVITY = 23
    private val REQUEST_CODE_PERMISSION = 24

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

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.syllabus_setting_activity
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

    override fun initRecycleView(items: List<Any>) {
        val adapter = MultiTypeAdapter()
        adapter.register(SeekBarItem::class, SeekBarBinder())
        adapter.register(CheckBoxItem::class, CheckBoxBinder())
        adapter.register(TextViewItem::class, TextViewBinder())
        adapter.register(ColorItem::class, ColorBinder())
        adapter.items = items
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        context.getDrawable(R.drawable.shape_divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        rv_syllabus_setting.addItemDecoration(dividerItemDecoration)
        rv_syllabus_setting.layoutManager = LinearLayoutManager(this)
        rv_syllabus_setting.adapter = adapter
    }

    override fun showPicturePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        } else {
            mPicturePicker.forResult(REQUEST_CODE_CHOOSE_ACTIVITY)
        }
    }

    override fun showColorPicker(setting: SyllabusSetting, ivColor: ImageView) {
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
            }
            negativeButton(text = "取消")
        }
    }

    override fun finish() {
        GlobalScope.launch {
            mPresenter.onFinish()
            launch(Dispatchers.Main) {
                super.finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CHOOSE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            mPresenter.onPictureSelect(Matisse.obtainResult(data)[0].toString())
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
