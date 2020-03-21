package cn.ifafu.ifafu.ui.main.new_theme

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseFragment
import cn.ifafu.ifafu.data.bean.Menu
import cn.ifafu.ifafu.databinding.FragmentMainNewBinding
import cn.ifafu.ifafu.ui.activity.AboutActivity
import cn.ifafu.ifafu.ui.elective.ElectiveActivity
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.score_list.ScoreListActivity
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import cn.ifafu.ifafu.ui.web.WebActivity
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.view.adapter.MenuAdapter
import cn.ifafu.ifafu.view.custom.DragLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_main_new.*
import kotlinx.android.synthetic.main.include_main_new_left_menu.*
import kotlinx.android.synthetic.main.main_include.*
import kotlinx.android.synthetic.main.main_next_course_include.*

class MainNewFragment : BaseFragment<FragmentMainNewBinding, MainViewModel>(), View.OnClickListener {

    private var mMenuAdapter: MenuAdapter? = null

    private val mVViewModel: MainNewViewModel by viewModels()

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_new
    }

    override fun getViewModel(): MainViewModel {
        return VMProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun initFragment(savedInstanceState: Bundle?) {
        btn_menu.setOnClickListener(this)
        tv_nav_about.setOnClickListener(this)
        tv_nav_feedback.setOnClickListener(this)
        tv_nav_setting.setOnClickListener(this)
        tv_nav_update.setOnClickListener(this)
        tv_nav_checkout.setOnClickListener(this)
        mBinding.vm = mVViewModel
        initMenu()
        mViewModel.timeAxis.observe(this, Observer {
            view_timeline.setTimeAxisList(it)
                    .invalidate()
        })
        mViewModel.nextCourse.observe(this, Observer {
            if (it.hasInfo) {
                val title = if (it.isInClass) {
                    getString(R.string.now_class_format, it.nextClass)
                } else {
                    getString(R.string.next_class_format, it.nextClass)
                }
                setCourseText(it.nextClass, title, it.address + "   " + it.classTime, it.timeLeft)
            } else {
                setCourseText(it.message, "", "", "")
            }
        })
        mViewModel.initFragmentData()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.updateNextCourse()
        mVViewModel.updateWeather()
        mViewModel.updateTimeAxis()
    }

    private fun initMenu() {
        val menus = listOf(
                Menu(R.drawable.tab_syllabus, "课程表", SyllabusActivity::class.java),
                Menu(R.drawable.tab_exam, "考试计划", ExamListActivity::class.java),
                Menu(R.drawable.tab_score, "成绩查询", ScoreListActivity::class.java),
                Menu(R.drawable.tab_elective, "选修查询", ElectiveActivity::class.java),
                Menu(R.drawable.tab_web, "网页模式", WebActivity::class.java),
                Menu(R.drawable.tab_electricity, "电费查询", ElectricityActivity::class.java),
                Menu(R.drawable.tab_repair, "报修服务", WebActivity::class.java),
                Menu(R.drawable.tab_feedback, "反馈问题", FeedbackActivity::class.java)
        )
        mMenuAdapter = MenuAdapter(requireContext(), menus).apply {
            setOnMenuClickListener { _, menu ->
                if (!ButtonUtils.isFastDoubleClick(Constant.ACTIVITY_MAIN)) {
                    if (menu.title == "报修服务") {
                        startActivity(Intent(activity, menu.activityClass).apply {
                            putExtra("title", "报修服务")
                            putExtra("url", Constant.REPAIR_URL)
                        })
                    } else {
                        startActivity(Intent(activity, menu.activityClass))
                    }
                }
            }
        }
        rv_menu.layoutManager = GridLayoutManager(
                context, 4, RecyclerView.VERTICAL, false)
        rv_menu.adapter = mMenuAdapter
    }

    private fun setLeftMenuHeadIcon(headIcon: Drawable) {
        Glide.with(this)
                .load(headIcon)
                .into(iv_menu_icon)
    }

    private fun setLeftMenuHeadName(name: String) {
        tv_menu_name.text = name
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_menu -> drawer_main.open()
            R.id.tv_nav_update -> mViewModel.upgradeApp()
            R.id.tv_nav_about -> startActivity(Intent(context, AboutActivity::class.java))
            R.id.tv_nav_feedback -> startActivity(Intent(context, FeedbackActivity::class.java))
            R.id.tv_nav_setting -> {
                requireActivity().startActivityForResult(Intent(context, SettingActivity::class.java), Constant.ACTIVITY_SETTING)
            }
            R.id.tv_nav_checkout -> mViewModel.switchAccount()
        }
        if (drawer_main.status == DragLayout.Status.Open) {
            drawer_main.close(true)
        }
    }

    private fun setCourseText(title: String, name: String, address: String, time: String) {
        tv_course_title.text = title
        tv_course_name.text = name
        tv_course_address.text = address
        tv_course_time.text = time
    }

}