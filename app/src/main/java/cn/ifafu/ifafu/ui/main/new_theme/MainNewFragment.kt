package cn.ifafu.ifafu.ui.main.new_theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseFragment
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.data.bean.Menu
import cn.ifafu.ifafu.databinding.FragmentMainNewBinding
import cn.ifafu.ifafu.experiment.ui.elective.ElectiveActivity
import cn.ifafu.ifafu.experiment.ui.score.ScoreActivity
import cn.ifafu.ifafu.ui.activity.AboutActivity
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.schedule.SyllabusActivity
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.ui.view.adapter.MenuAdapter
import cn.ifafu.ifafu.ui.view.custom.DragLayout
import cn.ifafu.ifafu.ui.view.listener.OnMenuItemClickListener
import cn.ifafu.ifafu.ui.web.WebActivity
import kotlinx.android.synthetic.main.fragment_main_new.*
import kotlinx.android.synthetic.main.include_main_new.*
import kotlinx.android.synthetic.main.include_main_new_course.*
import kotlinx.android.synthetic.main.include_main_new_left_menu.*

class MainNewFragment : BaseFragment(), View.OnClickListener, OnMenuItemClickListener {

    private val viewModel: MainNewViewModel by viewModels { getViewModelFactory() }
    private val activityViewModel: MainViewModel by activityViewModels { getViewModelFactory() }

    override fun layoutRes(): Int = R.layout.fragment_main_new

    override fun afterOnActivityCreated(savedInstanceState: Bundle?) {
        btn_menu.setOnClickListener(this)
        tv_nav_about.setOnClickListener(this)
        tv_nav_feedback.setOnClickListener(this)
        tv_nav_setting.setOnClickListener(this)
        tv_nav_update.setOnClickListener(this)
        tv_nav_checkout.setOnClickListener(this)
        bind<FragmentMainNewBinding>().vm = viewModel
        initMenu()
        viewModel.timeEvents.observe(this, Observer {
            timeline.setTimeEvents(it)
        })
        viewModel.nextCourse.observe(this, Observer {
            if (it.hasInfo) {
                val title = if (it.isInClass) {
                    getString(R.string.now_class)
                } else {
                    getString(R.string.next_class)
                }
                setCourseText(title, it.nextClass, it.address + "   " + it.classTime, it.timeLeft)
            } else {
                setCourseText(it.message, "", "", "")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateNextCourse()
        viewModel.updateWeather()
        viewModel.updateTimeAxis()
    }

    private fun initMenu() {
        val menus = mutableListOf(
                Menu(R.id.menu_schedule, R.drawable.tab_syllabus, "课程表", SyllabusActivity::class.java),
                Menu(R.id.menu_exam_list, R.drawable.tab_exam, "考试计划", ExamListActivity::class.java),
                Menu(R.id.menu_score_list, R.drawable.tab_score, "成绩查询", ScoreActivity::class.java),
                Menu(R.id.menu_elective, R.drawable.tab_elective, "选修查询", ElectiveActivity::class.java),
                Menu(R.id.menu_web, R.drawable.tab_web, "网页模式", WebActivity::class.java),
                Menu(R.id.menu_electricity, R.drawable.tab_electricity, "电费查询", ElectricityActivity::class.java),
                Menu(R.id.menu_repair, R.drawable.tab_repair, "报修服务", WebActivity::class.java),
                Menu(R.id.menu_feedback, R.drawable.tab_feedback, "反馈问题", FeedbackActivity::class.java)
        )
        val menuAdapter = MenuAdapter(this)
        menuAdapter.data = menus
        rv_menu.adapter = menuAdapter
    }

    override fun onMenuItemClick(menu: Menu) {
        when (menu.id) {
            R.id.menu_schedule ->
                startActivityByClazz(SyllabusActivity::class.java)
            R.id.menu_exam_list ->
                startActivityByClazz(ExamListActivity::class.java)
            R.id.menu_score_list ->
                startActivityByClazz(ScoreActivity::class.java)
//                findNavController().navigate(R.id.action_fragment_main_new_to_fragment_score_list)
            R.id.menu_elective ->
                startActivityByClazz(ElectiveActivity::class.java)
            R.id.menu_web ->
                startActivityByClazz(WebActivity::class.java)
            R.id.menu_electricity ->
                startActivityByClazz(ElectricityActivity::class.java)
            R.id.menu_repair -> {
                startActivity(Intent(activity, menu.activityClass).apply {
                    putExtra("title", "报修服务")
                    putExtra("url", Constant.REPAIR_URL)
                })
            }
            R.id.menu_feedback -> {
                startActivityByClazz(FeedbackActivity::class.java)
//                findNavController().navigate(R.id.action_fragment_main_new_to_feedbackFragment)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_menu -> drawer_main.open()
            R.id.tv_nav_update -> activityViewModel.upgradeApp()
            R.id.tv_nav_about -> startActivity(Intent(context, AboutActivity::class.java))
            R.id.tv_nav_feedback -> startActivity(Intent(context, FeedbackActivity::class.java))
            R.id.tv_nav_setting -> {
                requireActivity().startActivityForResult(Intent(context, SettingActivity::class.java), Constant.ACTIVITY_SETTING)
            }
            R.id.tv_nav_checkout -> activityViewModel.switchAccount()
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