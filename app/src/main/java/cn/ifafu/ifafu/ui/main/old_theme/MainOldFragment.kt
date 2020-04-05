package cn.ifafu.ifafu.ui.main.old_theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseFragment
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.data.bean.Menu
import cn.ifafu.ifafu.databinding.FragmentMainOldBinding
import cn.ifafu.ifafu.experiment.ui.elective.ElectiveActivity
import cn.ifafu.ifafu.experiment.ui.score.ScoreActivity
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.schedule.SyllabusActivity
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.ui.view.custom.MenuMaker
import cn.ifafu.ifafu.ui.view.listener.OnMenuItemClickListener
import cn.ifafu.ifafu.ui.view.listener.ScrollDrawerListener
import cn.ifafu.ifafu.ui.web.WebActivity
import com.google.android.material.navigation.NavigationView

class MainOldFragment : BaseFragment(), View.OnClickListener, OnMenuItemClickListener {

    private val activityViewModel: MainViewModel by activityViewModels { getViewModelFactory() }

    private val mViewModel: MainOldViewModel by viewModels { getViewModelFactory() }

    private lateinit var mDrawerLayout: DrawerLayout

    override fun layoutRes(): Int = R.layout.fragment_main_old

    override fun afterOnActivityCreated(savedInstanceState: Bundle?) {
        bind<FragmentMainOldBinding>().apply {
            viewModel = mViewModel
            mDrawerLayout = layoutDrawer
            initLeftMenu(root)
            initEvent(root)
        }
    }

    private fun initEvent(rootView: View) {
        //DataBinding无法设置Include布局的OnClickListener
        rootView.findViewById<View>(R.id.layout_exam_preview).setOnClickListener(this)
        rootView.findViewById<View>(R.id.layout_class_preview).setOnClickListener(this)
        rootView.findViewById<View>(R.id.layout_score_preview).setOnClickListener(this)
        rootView.findViewById<View>(R.id.tv_name).setOnClickListener(this)
        val leftMenuLayout = rootView.findViewById<NavigationView>(R.id.nav_left_menu)
        val contentLayout = rootView.findViewById<LinearLayout>(R.id.layout_content)
        if (leftMenuLayout != null && contentLayout != null) {
            val drawerListener = ScrollDrawerListener(leftMenuLayout, contentLayout)
            mDrawerLayout.addDrawerListener(drawerListener)
        }
    }

    override fun onStart() {
        super.onStart()
        mDrawerLayout.closeDrawer(GravityCompat.START)
        mViewModel.updateScorePreview()
        mViewModel.updateExamsPreview()
        mViewModel.updateClassPreview()
        mViewModel.updateWeather()
    }

    private fun initLeftMenu(rootView: View) {
        val menuLayout = rootView.findViewById<LinearLayout>(R.id.layout_left_menu) ?: return
        val menu = mapOf(
                "信息查询" to listOf(
                        Menu(R.id.menu_exam_list, R.drawable.menu_score_white, "成绩查询"),
                        Menu(R.id.menu_exam_list, R.drawable.menu_exam_white, "学生考试查询"),
                        Menu(R.id.menu_electricity, R.drawable.menu_elec_white, "电费查询"),
                        Menu(R.id.menu_elective, R.drawable.menu_elective_white, "选修学分查询")),
                "实用工具" to listOf(
                        Menu(R.id.menu_schedule, R.drawable.menu_syllabus_white, "我的课表"),
                        Menu(R.id.menu_web, R.drawable.menu_web_white, "网页模式"),
                        Menu(R.id.menu_repair, R.drawable.main_old_tabs_repair, "报修服务")),
                "软件设置" to listOf(
                        Menu(R.id.menu_setting, R.drawable.menu_setting_white, "软件设置"),
                        Menu(R.id.menu_user_management, R.drawable.main_old_tabs_manage, "账号管理")),
                "关于软件" to listOf(
                        Menu(R.id.menu_upgrade, R.drawable.menu_update_white, "检查更新"),
                        Menu(R.id.menu_about_ifafu, R.drawable.main_old_tabs_about, "关于iFAFU"),
                        Menu(R.id.menu_feedback, R.drawable.ic_feedback_white, "反馈问题")
                )
        )
        MenuMaker(menu)
                .layout(menuLayout)
                .menuClickListener(this)
                .make()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_name -> mDrawerLayout.openDrawer(GravityCompat.START)
            R.id.layout_exam_preview -> startActivityByClazz(ExamListActivity::class.java)
            R.id.layout_class_preview -> startActivityByClazz(SyllabusActivity::class.java)
            R.id.layout_score_preview -> startActivityByClazz(ScoreActivity::class.java)
        }
    }

    override fun onMenuItemClick(menu: Menu) {
        when (menu.id) {
            R.id.menu_exam_list ->
                startActivityByClazz(ExamListActivity::class.java)
            R.id.menu_score_list ->
                startActivityByClazz(ScoreActivity::class.java)
//                findNavController().navigate(R.id.action_fragment_main_old_to_fragment_score_list)
            R.id.menu_electricity ->
                startActivityByClazz(ElectricityActivity::class.java)
            R.id.menu_elective ->
                startActivityByClazz(ElectiveActivity::class.java)
            R.id.menu_schedule ->
                startActivityByClazz(SyllabusActivity::class.java)
            R.id.menu_web ->
                startActivityByClazz(WebActivity::class.java)
            R.id.menu_repair ->
                startActivity(Intent(activity, menu.activityClass).apply {
                    putExtra("title", "报修服务")
                    putExtra("url", Constant.REPAIR_URL)
                })
            R.id.menu_setting ->
                startActivityByClazz(SettingActivity::class.java)
            R.id.menu_user_management ->
                activityViewModel.switchAccount()
            R.id.menu_upgrade ->
                activityViewModel.upgradeApp()
            R.id.menu_feedback ->
                startActivityByClazz(FeedbackActivity::class.java)
//                findNavController().navigate(R.id.action_fragment_main_old_to_feedbackFragment)
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
    }

}