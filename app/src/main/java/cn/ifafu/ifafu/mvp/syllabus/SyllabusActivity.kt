package cn.ifafu.ifafu.mvp.syllabus

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.mvp.BaseActivity
import cn.ifafu.ifafu.entity.Course
import cn.ifafu.ifafu.entity.SyllabusSetting
import cn.ifafu.ifafu.mvp.main.MainActivity
import cn.ifafu.ifafu.mvp.syllabus_item.SyllabusItemActivity
import cn.ifafu.ifafu.mvp.syllabus_setting.SyllabusSettingActivity
import cn.ifafu.ifafu.util.ChineseNumbers
import cn.ifafu.ifafu.view.adapter.SyllabusPageAdapter
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import cn.ifafu.ifafu.view.syllabus.CourseBase
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.syllabus_activity.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SyllabusActivity : BaseActivity<SyllabusContract.Presenter>(), SyllabusContract.View,
        View.OnClickListener, View.OnLongClickListener {

    private var mCurrentWeek = 1
    private lateinit var mPageAdapter: SyllabusPageAdapter
    private lateinit var loadingDialog: LoadingDialog

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.syllabus_activity
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_syllabus)
                .statusBarDarkFont(true)
                .init()

        mPresenter = SyllabusPresenter(this)

        loadingDialog = LoadingDialog(this)
        loadingDialog.setText("加载中")
        loadingDialog.setOnCancelListener {
            mPresenter?.cancelLoading()
        }

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

        mPageAdapter = SyllabusPageAdapter(setting, ArrayList()) {
            val intent = Intent(this, SyllabusItemActivity::class.java)
            intent.putExtra("course_id", (it.getOther() as Course).id)
            startActivityForResult(intent, Constant.ACTIVITY_SYLLABUS_ITEM)
        }
        view_pager.adapter = mPageAdapter
        setCurrentWeek(setting.currentWeek)
        Log.d(TAG, "background: ${setting.background}")

        if (setting.background.isNotEmpty()) {
//            Glide.with(this)
//                    .load(setting.background)
//                    .skipMemoryCache(true)
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .into(iv_background)
            iv_background.setImageDrawable(null)
            iv_background.setImageURI(Uri.parse(setting.background))
        } else {
            iv_background.setImageDrawable(null)
        }
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

    override fun setSyllabusData(courses: MutableList<MutableList<CourseBase>?>) {
        mPageAdapter.courses = courses
    }

    override fun showLoading() {
        loadingDialog.show()
    }

    override fun hideLoading() {
        loadingDialog.cancel()
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
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN) {
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
            mPresenter.updateSyllabusLocal()
        } else if (requestCode == Constant.ACTIVITY_SYLLABUS_SETTING && resultCode == Activity.RESULT_OK) {
            mPresenter.updateSyllabusSetting()
            mPresenter.updateSyllabusLocal()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val BUTTON_ADD = 0
    }
}