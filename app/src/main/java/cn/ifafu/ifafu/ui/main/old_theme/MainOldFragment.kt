package cn.ifafu.ifafu.ui.main.old_theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.databinding.FragmentMainOldBinding
import cn.ifafu.ifafu.ui.activity.AboutActivity
import cn.ifafu.ifafu.ui.elective.ElectiveActivity
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.main.bean.Menu
import cn.ifafu.ifafu.ui.score_list.ScoreListActivity
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import cn.ifafu.ifafu.ui.web.WebActivity
import cn.ifafu.ifafu.view.custom.MenuClickListener
import cn.ifafu.ifafu.view.custom.MenuMaker
import cn.ifafu.ifafu.view.listener.ScrollDrawerListener
import cn.woolsen.easymvvm.base.BaseFragment
import com.google.android.material.navigation.NavigationView
import timber.log.Timber

class MainOldFragment : BaseFragment(), MenuClickListener, View.OnClickListener {

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
        val leftMenuLayout = rootView.findViewById<LinearLayout>(R.id.layout_left_menu) ?: return
        val menu = Menu(mapOf(
                "信息查询" to listOf(
                        Menu.Item("成绩查询", R.drawable.menu_score_white),
                        Menu.Item("学生考试查询", R.drawable.menu_exam_white),
                        Menu.Item("电费查询", R.drawable.menu_elec_white),
                        Menu.Item("选修学分查询", R.drawable.menu_elective_white)),
                "实用工具" to listOf(
                        Menu.Item("我的课表", R.drawable.menu_syllabus_white),
                        Menu.Item("网页模式", R.drawable.menu_web_white),
//                        Menu.Item("一键评教", R.drawable.main_old_tabs_comment),
                        Menu.Item("报修服务", R.drawable.main_old_tabs_repair)),
                "软件设置" to listOf(
                        Menu.Item("软件设置", R.drawable.menu_setting_white),
                        Menu.Item("账号管理", R.drawable.main_old_tabs_manage)),
                "关于软件" to listOf(
                        Menu.Item("检查更新", R.drawable.menu_update_white),
                        Menu.Item("关于iFAFU", R.drawable.main_old_tabs_about),
                        Menu.Item("反馈问题", R.drawable.ic_feedback_white)
                )
        ))
        MenuMaker(menu)
                .layout(leftMenuLayout)
                .menuClickListener(this)
                .make()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_name -> mDrawerLayout.openDrawer(GravityCompat.START)
            R.id.layout_exam_preview -> startActivity(Intent(activity, ExamListActivity::class.java))
            R.id.layout_class_preview -> startActivity(Intent(activity, SyllabusActivity::class.java))
            R.id.layout_score_preview -> startActivity(Intent(activity, ScoreListActivity::class.java))
        }
    }

    override fun onMenuClick(item: Menu.Item) {
        when (item.title) {
            "成绩查询" -> startActivity(Intent(activity, ScoreListActivity::class.java))
            "学生考试查询" -> startActivity(Intent(activity, ExamListActivity::class.java))
            "我的课表" -> startActivity(Intent(activity, SyllabusActivity::class.java))
            "网页模式" -> startActivity(Intent(activity, WebActivity::class.java))
            "电费查询" -> startActivity(Intent(activity, ElectricityActivity::class.java))
            "报修服务" -> {
                startActivity(Intent(activity, WebActivity::class.java).apply {
                    putExtra("title", "报修服务")
                    putExtra("url", Constant.REPAIR_URL)
                })
            }
            "选修学分查询" -> startActivity(Intent(activity, ElectiveActivity::class.java))
//            "一键评教" -> startActivity(Intent(context, CommentActivity::class.java))
            "账号管理" -> activityViewModel.switchAccount()
            "软件设置" -> requireActivity().startActivityForResult(Intent(context, SettingActivity::class.java), Constant.ACTIVITY_SETTING)
            "检查更新" -> activityViewModel.upgradeApp()
            "关于iFAFU" -> startActivity(Intent(activity, AboutActivity::class.java))
            "反馈问题" -> startActivity(Intent(activity, FeedbackActivity::class.java))
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
    }

}