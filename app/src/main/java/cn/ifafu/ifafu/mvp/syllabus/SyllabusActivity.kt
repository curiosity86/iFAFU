package cn.ifafu.ifafu.mvp.syllabus

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.mvp.main.MainActivity
import cn.ifafu.ifafu.mvp.syllabus_item.SyllabusItemActivity
import cn.ifafu.ifafu.mvp.syllabus_setting.SyllabusSettingActivity
import cn.ifafu.ifafu.util.ChineseNumbers
import cn.ifafu.ifafu.view.adapter.SyllabusPageAdapter
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import cn.ifafu.ifafu.view.syllabus.CourseBase
import cn.ifafu.ifafu.view.syllabus.CourseView.OnCourseClickListener
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_syllabus.*
import java.text.SimpleDateFormat
import java.util.*

class SyllabusActivity : BaseActivity<SyllabusContract.Presenter>(), SyllabusContract.View,
        View.OnClickListener, View.OnLongClickListener {

    private var mPageAdapter: SyllabusPageAdapter? = null
    private var mCurrentWeek = 1
    private var progressDialog: ProgressDialog? = null

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_syllabus
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_syllabus)
                .statusBarDarkFont(true)
                .init()

        mPresenter = SyllabusPresenter(this)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setText("加载中")

        btn_back.setOnClickListener(this)
        btn_add.setOnClickListener(this)
        btn_refresh.setOnClickListener(this)
        btn_setting.setOnClickListener(this)

        tv_date.text = SimpleDateFormat("MM月dd日", Locale.CHINA).format(Date())
        tv_subtitle.setOnLongClickListener(this)
    }

    override fun setSyllabusSetting(setting: SyllabusSetting) {
        val themeColor = setting.themeColor
        tv_date.setTextColor(themeColor)
        tv_subtitle.setTextColor(themeColor)
        btn_back.setColorFilter(themeColor)
        btn_add.setColorFilter(themeColor)
        btn_refresh.setColorFilter(themeColor)
        btn_setting.setColorFilter(themeColor)
        ImmersionBar.with(this)
                .statusBarDarkFont(setting.statusDartFont)
                .init()
        if (mPageAdapter == null) {
            initSyllabusData(null, setting)
        } else {
            mPageAdapter!!.setting = setting
            setCurrentWeek(setting.currentWeek)
        }
//        if (setting.background != null) {
            println("Load Custom Background ${setting.background}")
//        Glide.with(this)
//                .load(setting.background)
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .into()
//        rr_background.background =
//            rr_background.background = BitmapDrawable
//        } else {
//            println("Load White Background")
//            rr_background.setBackgroundColor(Color.WHITE)
//        }
    }

    override fun setCurrentWeek(currentWeek: Int) {
        mCurrentWeek = currentWeek
        view_pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val numCN: String? = ChineseNumbers.englishNumberToChinese((position + 1).toString())
                if (mCurrentWeek - 1 < 0) {
                    if (position != 0) {
                        tv_subtitle.text = getString(R.string.week_format_not_return, numCN, "第一周")
                    } else {
                        tv_subtitle.text = getString(R.string.week_format_not, numCN)
                    }
                } else if (position != mCurrentWeek - 1) {
                    if (mCurrentWeek < 0) {
                        tv_subtitle.text = getString(R.string.week_format_not_return, numCN, "第一周")
                    } else {
                        tv_subtitle.text = getString(R.string.week_format_not_return, numCN, "本周")
                    }
                } else {
                    tv_subtitle.text = getString(R.string.week_format, numCN)
                }
            }
        })
        view_pager.setCurrentItem(currentWeek - 1, false)
    }

    override fun setSyllabusDate(courses: MutableList<MutableList<CourseBase>?>) {
        if (mPageAdapter == null) {
            initSyllabusData(courses, null)
        } else {
            mPageAdapter!!.courses = courses
        }
    }

    private fun initSyllabusData(courses: MutableList<MutableList<CourseBase>?>?, setting: SyllabusSetting?) {
        if (courses == null) {
            mPageAdapter = SyllabusPageAdapter(setting!!)
        } else if (setting == null) {
            mPageAdapter = SyllabusPageAdapter(courses)
        } else {
            mPageAdapter = SyllabusPageAdapter(courses, setting)
        }
        mPageAdapter!!.onCourseClickListener = OnCourseClickListener { _, course: CourseBase? ->
            val intent = Intent(this, SyllabusItemActivity::class.java)
            intent.putExtra("course_id", (course!!.getOther() as Course).id)
            startActivityForResult(intent, Constant.ACTIVITY_SYLLABUS_ITEM)
        }
        view_pager.adapter = mPageAdapter
        if (setting != null) {
            setCurrentWeek(setting.currentWeek)
        }
    }

    override fun showLoading() {
        progressDialog!!.show()
    }

    override fun hideLoading() {
        progressDialog!!.cancel()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add -> {
                val intent = Intent(this, SyllabusItemActivity::class.java)
                intent.putExtra("come_from", BUTTON_ADD)
                startActivityForResult(intent, Constant.ACTIVITY_SYLLABUS_ITEM)
            }
            R.id.btn_refresh -> mPresenter.updateSyllabusNet()
            R.id.btn_back -> onFinishActivity()
            R.id.btn_setting -> startActivityForResult(
                    Intent(this, SyllabusSettingActivity::class.java),
                    Constant.ACTIVITY_SYLLABUS_SETTING)
        }
    }

    override fun onLongClick(v: View?): Boolean {
        return when (v?.id) {
            R.id.tv_subtitle -> {
                view_pager.setCurrentItem(mCurrentWeek - 1, true)
                true
            }
            else -> false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_DOWN) {
            onFinishActivity()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun onFinishActivity() {
        when (intent.getIntExtra("from", -1)) {
            Constant.SYLLABUS_WIDGET, Constant.ACTIVITY_SPLASH -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.ACTIVITY_SYLLABUS_ITEM && resultCode == Activity.RESULT_OK) {
            mPresenter!!.updateSyllabusLocal()
            return
        } else if (requestCode == Constant.ACTIVITY_SYLLABUS_SETTING) {
            mPresenter!!.updateSyllabusSetting()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val BUTTON_ADD = 0
    }
}