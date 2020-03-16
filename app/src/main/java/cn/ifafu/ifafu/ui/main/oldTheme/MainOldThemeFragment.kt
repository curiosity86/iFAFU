package cn.ifafu.ifafu.ui.main.oldTheme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.databinding.FragmentMainOldThemeBinding
import cn.ifafu.ifafu.ui.activity.AboutActivity
import cn.ifafu.ifafu.ui.elective.ElectiveActivity
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.main.oldTheme.bean.Menu
import cn.ifafu.ifafu.ui.score_list.ScoreListActivity
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import cn.ifafu.ifafu.ui.web.WebActivity
import cn.ifafu.ifafu.view.custom.LeftMenu
import cn.ifafu.ifafu.view.listener.ScrollDrawerListener
import cn.ifafu.ifafu.view.listener.TabClickListener
import com.google.android.material.navigation.NavigationView
import org.koin.android.viewmodel.ext.android.viewModel

class MainOldThemeFragment : Fragment(), TabClickListener, View.OnClickListener {

    private lateinit var binding: FragmentMainOldThemeBinding

    private val activityViewModel by lazy {
        VMProvider(requireActivity())[MainViewModel::class.java]
    }

    private val mViewModel: MainOldThemeViewModel by viewModel()

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_old_theme, container, false)
        binding = FragmentMainOldThemeBinding.bind(view).apply {
            viewModel = mViewModel
        }
        binding.lifecycleOwner = this
        drawerLayout = view.findViewById(R.id.layout_drawer)
        initLeftMenu(view)
        initEvent(view)
        return view
    }

    private fun initEvent(rootView: View) {
        rootView.findViewById<View>(R.id.layout_exam_preview).setOnClickListener(this)
        rootView.findViewById<View>(R.id.layout_class_preview).setOnClickListener(this)
        rootView.findViewById<View>(R.id.layout_score_preview).setOnClickListener(this)
        rootView.findViewById<View>(R.id.tv_name).setOnClickListener(this)
        val leftMenuLayout = rootView.findViewById<NavigationView>(R.id.nav_left_menu)
        val contentLayout = rootView.findViewById<LinearLayout>(R.id.layout_content)
        if (leftMenuLayout != null && contentLayout != null) {
            val drawerListener =  ScrollDrawerListener(leftMenuLayout, contentLayout)
            rootView.findViewById<DrawerLayout>(R.id.layout_drawer).addDrawerListener(drawerListener)
        }
    }


    override fun onStart() {
        super.onStart()
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
                        Menu.Item("报修服务", R.drawable.main_old_tabs_repair),
                        Menu.Item("一键评教", R.drawable.main_old_tabs_comment)),
                "软件设置" to listOf(
                        Menu.Item("软件设置", R.drawable.menu_setting_white),
                        Menu.Item("账号管理", R.drawable.main_old_tabs_manage)),
                "关于软件" to listOf(
                        Menu.Item("检查更新", R.drawable.menu_update_white),
                        Menu.Item("关于iFAFU", R.drawable.main_old_tabs_about))
        ))
        LeftMenu(leftMenuLayout)
                .make(menu)
                .setTabClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_name -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.layout_exam_preview -> startActivity(Intent(activity, ExamListActivity::class.java))
            R.id.layout_class_preview -> startActivity(Intent(activity, SyllabusActivity::class.java))
            R.id.layout_score_preview -> startActivity(Intent(activity, ScoreListActivity::class.java))
        }
    }

    override fun onTabClick(tabName: String) {
        when (tabName) {
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
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

}