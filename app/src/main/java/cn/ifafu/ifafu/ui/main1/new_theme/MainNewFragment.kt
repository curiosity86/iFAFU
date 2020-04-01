package cn.ifafu.ifafu.ui.main1.new_theme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.data.bean.Menu
import cn.ifafu.ifafu.databinding.FragmentMainNewBinding
import cn.ifafu.ifafu.experiment.score.ScoreActivity
import cn.ifafu.ifafu.ui.activity.AboutActivity
import cn.ifafu.ifafu.experiment.elective.ElectiveActivity
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import cn.ifafu.ifafu.ui.schedule.SyllabusActivity
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.ui.web.WebActivity
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.view.adapter.MenuAdapter
import cn.ifafu.ifafu.view.custom.DragLayout
import kotlinx.android.synthetic.main.fragment_main_new.*
import kotlinx.android.synthetic.main.include_main_new.*
import kotlinx.android.synthetic.main.include_main_new_course.*
import kotlinx.android.synthetic.main.include_main_new_left_menu.*

class MainNewFragment : Fragment(), View.OnClickListener {

    private val mMenuAdapter: MenuAdapter by lazy { MenuAdapter(requireContext()) }

    private val viewModel: MainNewViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMainNewBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            btn_menu.setOnClickListener(this@MainNewFragment)
            tv_nav_about.setOnClickListener(this@MainNewFragment)
            tv_nav_feedback.setOnClickListener(this@MainNewFragment)
            tv_nav_setting.setOnClickListener(this@MainNewFragment)
            tv_nav_update.setOnClickListener(this@MainNewFragment)
            tv_nav_checkout.setOnClickListener(this@MainNewFragment)
            vm = viewModel
        }
        initMenu()
        viewModel.timeEvents.observe(viewLifecycleOwner, Observer {
            timeline.setTimeEvents(it)
        })
        viewModel.nextCourse.observe(viewLifecycleOwner, Observer {
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
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateNextCourse()
        viewModel.updateWeather()
        viewModel.updateTimeAxis()
    }

    private fun initMenu() {
        val menus = listOf(
                Menu(R.drawable.tab_syllabus, "课程表", SyllabusActivity::class.java),
                Menu(R.drawable.tab_exam, "考试计划", ExamListActivity::class.java),
                Menu(R.drawable.tab_score, "成绩查询", ScoreActivity::class.java),
                Menu(R.drawable.tab_elective, "选修查询", ElectiveActivity::class.java),
                Menu(R.drawable.tab_web, "网页模式", WebActivity::class.java),
                Menu(R.drawable.tab_electricity, "电费查询", ElectricityActivity::class.java),
                Menu(R.drawable.tab_repair, "报修服务", WebActivity::class.java),
                Menu(R.drawable.tab_feedback, "反馈问题", FeedbackActivity::class.java)
        )
        mMenuAdapter.menus = menus
        mMenuAdapter.setOnMenuClickListener { _, menu ->
            when (menu.title) {
                "课程表" -> findNavController()
                        .navigate(R.id.action_fragment_main_new_to_fragment_score_list)
                "选修查询" -> findNavController()
                        .navigate(R.id.action_fragment_main_new_to_fragment_elective)
                else -> {
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
        }
        rv_menu.layoutManager = GridLayoutManager(
                context, 4, RecyclerView.VERTICAL, false)
        rv_menu.adapter = mMenuAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_menu -> drawer_main.open()
//            R.id.tv_nav_update -> activityViewModel.upgradeApp()
            R.id.tv_nav_about -> startActivity(Intent(context, AboutActivity::class.java))
            R.id.tv_nav_feedback -> startActivity(Intent(context, FeedbackActivity::class.java))
            R.id.tv_nav_setting -> {
                requireActivity().startActivityForResult(Intent(context, SettingActivity::class.java), Constant.ACTIVITY_SETTING)
            }
//            R.id.tv_nav_checkout -> activityViewModel.switchAccount()
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