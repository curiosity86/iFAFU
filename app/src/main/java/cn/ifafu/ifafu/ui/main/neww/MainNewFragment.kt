package cn.ifafu.ifafu.ui.main.neww

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseFragment
import cn.ifafu.ifafu.data.bean.Menu
import cn.ifafu.ifafu.data.bean.NextCourse
import cn.ifafu.ifafu.databinding.MainNewFragmentBinding
import cn.ifafu.ifafu.ui.activity.AboutActivity
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.setting.SettingActivity
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.view.adapter.MenuAdapter
import cn.ifafu.ifafu.view.custom.DragLayout
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.main_include.*
import kotlinx.android.synthetic.main.main_menu_include.*
import kotlinx.android.synthetic.main.main_new_fragment.*
import kotlinx.android.synthetic.main.main_next_course_include.*
import kotlinx.android.synthetic.main.main_weather_include.*

class MainNewFragment : BaseFragment<MainNewFragmentBinding, MainViewModel>(), View.OnClickListener {

    private var mMenuAdapter: MenuAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.main_new_fragment
    }

    override fun getViewModel(): MainViewModel {
        return VMProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun initFragment(savedInstanceState: Bundle?) {
        btn_menu.setOnClickListener(this)
        tv_nav_about.setOnClickListener(this)
        tv_nav_share.setOnClickListener(this)
        tv_nav_setting.setOnClickListener(this)
        tv_nav_update.setOnClickListener(this)
        tv_nav_checkout.setOnClickListener(this)
        mViewModel.weather.observe(this, Observer {
            tv_weather_1.text = "${it.nowTemp}℃" + ""
            tv_weather_2.text = "${it.cityName} | ${it.weather}"
        })
        mViewModel.schoolIcon.observe(this, Observer {
            setLeftMenuHeadIcon(it)
        })
        mViewModel.newThemeMenu.observe(this, Observer {
            setMenuAdapterData(it)
        })
        mViewModel.inUseUser.observe(this, Observer {
            setLeftMenuHeadName(it.name)
        })
        mViewModel.timeAxis.observe(this, Observer {
            view_timeline.setTimeAxisList(it)
                    .invalidate()
        })
        mViewModel.nextCourse.observe(this, Observer {
            when (it.result) {
                NextCourse.IN_HOLIDAY,
                NextCourse.EMPTY_DATA,
                NextCourse.NO_TODAY_COURSE,
                NextCourse.NO_NEXT_COURSE -> {
                    setCourseText(it.title, "", "", "")
                }
                NextCourse.HAS_NEXT_COURSE,
                NextCourse.IN_COURSE -> {
                    setCourseText(it.title, it.name, it.address + "   " + it.timeText, it.lastText)
                }
            }
        })
        mViewModel.initFragmentData()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.updateNextCourse()
        mViewModel.updateWeather()
        mViewModel.updateTimeAxis()
    }

    private fun setMenuAdapterData(menus: List<Menu>) {
        if (mMenuAdapter == null) {
            mMenuAdapter = MenuAdapter(requireContext(), menus)
            mMenuAdapter!!.setOnMenuClickListener { _, menu ->
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
            rv_menu.layoutManager = GridLayoutManager(
                    context, 4, RecyclerView.VERTICAL, false)
            rv_menu.adapter = mMenuAdapter
        } else {
            mMenuAdapter!!.setMenuList(menus)
        }
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
            R.id.tv_nav_share -> {
                //分享APP
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享")
                intent.putExtra(Intent.EXTRA_TEXT, "iFAFU下载链接：http://ifafu.cn")
                startActivity(Intent.createChooser(intent, "分享"))
            }
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