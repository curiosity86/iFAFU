package cn.ifafu.ifafu.ui.schedule

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.databinding.ActivityScheduleBinding
import cn.ifafu.ifafu.ui.main.MainActivity
import cn.ifafu.ifafu.ui.schedule.view.SyllabusPageAdapter
import cn.ifafu.ifafu.ui.schedule.view.listener.OnItemClickListener
import cn.ifafu.ifafu.ui.schedule.view.listener.OnItemLongClickListener
import cn.ifafu.ifafu.ui.schedule_item.CourseItemActivity
import cn.ifafu.ifafu.ui.schedule_setting.SyllabusSettingActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.util.ChineseNumbers
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_schedule.*
import java.text.SimpleDateFormat
import java.util.*

class SyllabusActivity : BaseActivity(), View.OnClickListener, View.OnLongClickListener {

    private var mCurrentWeek = 1
    private val mPageAdapter: SyllabusPageAdapter by lazy {
        SyllabusPageAdapter(OnItemClickListener { v, item ->
            val intent = Intent(this@SyllabusActivity, CourseItemActivity::class.java)
//            val options = ActivityOptions.makeSceneTransitionAnimation(
//                    this, v, "shared_element_end_root")
            intent.putExtra("course_id", item.tag as Int)
            startActivityForResult(intent, Constant.ACTIVITY_SYLLABUS_ITEM)
        }, OnItemLongClickListener { v, item ->
            PopupMenu(this, v, Gravity.BOTTOM).apply {
                setContentView(R.layout.md_listitem)
            }.show()
        })
    }
    private val loadingDialog = LoadingDialog(this)

    private val mViewModel: SyllabusViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_syllabus)
                .statusBarDarkFont(true)
                .init()
        with(bind<ActivityScheduleBinding>(R.layout.activity_schedule)) {
            vm = mViewModel
        }
        btn_back.setOnClickListener(this)
        btn_add.setOnClickListener(this)
        btn_refresh.setOnClickListener(this)
        btn_setting.setOnClickListener(this)
        view_pager.adapter = mPageAdapter
        tv_date.text = SimpleDateFormat("MM月dd日", Locale.CHINA).format(Date())
        tv_subtitle.setOnLongClickListener(this)
        mViewModel.setting.observe(this, Observer {
            setSyllabusSetting(it)
        })
        mViewModel.backgroundUri.observe(this, Observer {
            iv_background.setImageURI(it)
        })
        mViewModel.courses.observe(this, Observer {
            mPageAdapter.courses = it
            mPageAdapter.notifyDataSetChanged()
        })

        loadingDialog.observe(this, mViewModel.loading)
        mViewModel.initData()
    }

    private fun setSyllabusSetting(setting: SyllabusSetting) {
        val themeColor = setting.themeColor
        //设置主题色
        tv_date.setTextColor(themeColor)
        tv_subtitle.setTextColor(themeColor)
        btn_back.setColorFilter(themeColor)
        btn_add.setColorFilter(themeColor)
        btn_refresh.setColorFilter(themeColor)
        btn_setting.setColorFilter(themeColor)
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_syllabus)
                .statusBarDarkFont(setting.statusDartFont)
                .init()
        mPageAdapter.setting = setting
        mPageAdapter.notifyDataSetChanged()
        setCurrentWeek(setting.getCurrentWeek())
    }

    private fun setCurrentWeek(currentWeek: Int) {
        mCurrentWeek = currentWeek
        view_pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                val weekInChinese = "第${ChineseNumbers.englishNumberToChinese((position + 1).toString())}周"
                if (mCurrentWeek <= 0) {
                    if (position == 0) {
                        tv_subtitle.text = "$weekInChinese(放假中)"
                    } else {
                        tv_subtitle.text = "$weekInChinese 长按返回第一周"
                    }
                } else {
                    if (position == mCurrentWeek - 1) {
                        tv_subtitle.text = "$weekInChinese(本周)"
                    } else {
                        tv_subtitle.text = "$weekInChinese 长按返回本周"
                    }
                }
            }
        })
        view_pager.setCurrentItem(currentWeek - 1, false)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add -> {
                val intent = Intent(this, CourseItemActivity::class.java)
                intent.putExtra("come_from", BUTTON_ADD)
                startActivityForResult(intent, Constant.ACTIVITY_SYLLABUS_ITEM)
            }
            R.id.btn_refresh -> mViewModel.updateSyllabusNet()
            R.id.btn_back -> onFinishActivity()
            R.id.btn_setting -> startActivityForResult(
                    Intent(this, SyllabusSettingActivity::class.java),
                    Constant.ACTIVITY_SYLLABUS_SETTING)
        }
    }

    override fun onLongClick(v: View?): Boolean {
        return when (v?.id) {
            R.id.tv_subtitle -> {
                rollbackToCurrent()
                true
            }
            else -> false
        }
    }

    private fun rollbackToCurrent() {
        view_pager.setCurrentItem(mCurrentWeek - 1, true)
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
        when {
            resultCode != Activity.RESULT_OK -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
            requestCode == Constant.ACTIVITY_SYLLABUS_ITEM -> {
                mViewModel.updateSyllabusLocal()
            }
            requestCode == Constant.ACTIVITY_SYLLABUS_SETTING -> {
                mViewModel.updateSyllabusSetting()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    companion object {
        const val BUTTON_ADD = 0
    }

}