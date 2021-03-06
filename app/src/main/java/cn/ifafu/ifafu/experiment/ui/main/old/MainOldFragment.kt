package cn.ifafu.ifafu.experiment.ui.main.old

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseSimpleFragment
import cn.ifafu.ifafu.constant.ACTIVITY_LOGIN
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.data.bean.Menu
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.databinding.FragmentMainOld1Binding
import cn.ifafu.ifafu.experiment.ui.login.LoginActivity
import cn.ifafu.ifafu.ui.activity.AboutActivity
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.ui.main.view.MultiUserDialog
import cn.ifafu.ifafu.ui.schedule.SyllabusActivity
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.ui.view.custom.MenuMaker
import cn.ifafu.ifafu.ui.view.listener.OnMenuItemClickListener
import cn.ifafu.ifafu.ui.view.listener.ScrollDrawerListener
import cn.ifafu.ifafu.ui.web.WebActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.dialog_user_detail.view.*
import kotlinx.android.synthetic.main.fragment_main_old1.view.*
import kotlinx.android.synthetic.main.include_main_old_menu.view.*

class MainOldFragment : BaseSimpleFragment(), OnMenuItemClickListener, View.OnClickListener {


    private val mMultiUserDialog by lazy {
        MultiUserDialog(requireContext(), {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.putExtra("from", Constant.ACTIVITY_MAIN)
            startActivityForResult(intent, Constant.ACTIVITY_LOGIN)
        }, { user ->
            showAccountDetailDialog(user)
        })
    }
    private val mLoadingDialog by lazy { LoadingDialog(requireContext()) }

    private lateinit var mDrawerLayout: DrawerLayout

    private val mViewModel: MainOldViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMainOld1Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //沉浸状态栏
        ImmersionBar.with(this)
                .titleBar(view.tb_main_old)
                .init()

        //初始化点击事件
        view.layout_exam_preview.setOnClickListener(this)
        view.layout_class_preview.setOnClickListener(this)
        view.layout_score_preview.setOnClickListener(this)
        view.tv_name.setOnClickListener(this)

        //初始化菜单
        val leftMenuLayout = view.nav_left_menu
        val contentLayout = view.layout_content
        val drawerListener = ScrollDrawerListener(leftMenuLayout, contentLayout)
        mDrawerLayout = view.layout_drawer
        mDrawerLayout.addDrawerListener(drawerListener)
        initLeftMenu(view.layout_left_menu)

        //初始化ViewModel
        mViewModel.finish.observe(viewLifecycleOwner, Observer {
            requireActivity().finish()
        })
        mViewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrBlank()) {
                mLoadingDialog.cancel()
            } else {
                mLoadingDialog.show(it)
            }
        })
        mViewModel.message.observe(viewLifecycleOwner, Observer { toast(it) })
        mViewModel.users.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                showMultiAccountDialog(it)
            } else {
                mMultiUserDialog.cancel()
            }
        })
        mViewModel.startLoginActivity.observe(viewLifecycleOwner, Observer {
            startActivityForResult(Intent(requireContext(), LoginActivity::class.java), ACTIVITY_LOGIN)
        })
    }

    override fun onStart() {
        super.onStart()
        mDrawerLayout.closeDrawer(GravityCompat.START)
        mViewModel.updateScorePreview()
        mViewModel.updateExamsPreview()
        mViewModel.updateClassPreview()
        mViewModel.updateWeather()
    }

    private fun initLeftMenu(menuLayout: LinearLayout) {
        val menu = mapOf(
                "信息查询" to listOf(
                        Menu(R.id.menu_exam_list, R.drawable.menu_score_white, "考试查询"),
                        Menu(R.id.menu_score_list, R.drawable.menu_exam_white, "成绩查询"),
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

    override fun onMenuItemClick(menu: Menu) {
        when (menu.id) {
            R.id.menu_exam_list ->
                startActivityByClazz(ExamListActivity::class.java)
            R.id.menu_score_list ->
                findNavController().navigate(R.id.action_fragment_main_old_to_fragment_score_list)
            R.id.menu_electricity ->
                startActivityByClazz(ElectricityActivity::class.java)
            R.id.menu_elective ->
                findNavController().navigate(R.id.action_fragment_main_new_to_fragment_elective)
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
                mViewModel.switchAccount()
            R.id.menu_upgrade ->
                mViewModel.upgradeApp()
            R.id.menu_about_ifafu ->
                startActivityByClazz(AboutActivity::class.java)
            R.id.menu_feedback ->
                findNavController().navigate(R.id.action_fragment_main_old_to_feedbackFragment)
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_name ->
                mDrawerLayout.openDrawer(GravityCompat.START)
            R.id.layout_exam_preview ->
                startActivityByClazz(ExamListActivity::class.java)
            R.id.layout_class_preview ->
                startActivityByClazz(SyllabusActivity::class.java)
            R.id.layout_score_preview ->
                findNavController().navigate(R.id.action_fragment_main_old_to_fragment_score_list)
        }
    }

    private fun showMultiAccountDialog(users: List<User>) {
        mMultiUserDialog.apply {
            setUsers(users)
        }.show()
    }

    private fun showAccountDetailDialog(user: User) {
        MaterialDialog(requireContext()).show {
            title(text = "${user.name} ${user.account}")
            customView(viewRes = R.layout.dialog_user_detail)
            getCustomView().et_password.setText(user.password)
            negativeButton(text = "删除账号") {
                mViewModel.deleteUser(user)
            }
            positiveButton(text = "切换账号") {
                mViewModel.checkoutTo(user)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_LOGIN && resultCode == Activity.RESULT_OK) {
            mViewModel.addAccountSuccessful()
        }
    }
}