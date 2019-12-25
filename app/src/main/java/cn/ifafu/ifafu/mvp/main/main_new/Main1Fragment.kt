package cn.ifafu.ifafu.mvp.main.main_new

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.entity.Menu
import cn.ifafu.ifafu.mvp.activity.AboutActivity
import cn.ifafu.ifafu.mvp.main.BaseMainFragment
import cn.ifafu.ifafu.mvp.setting.SettingActivity
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.view.adapter.MenuAdapter
import cn.ifafu.ifafu.view.custom.DragLayout
import cn.ifafu.ifafu.view.timeline.TimeAxis
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_include.*
import kotlinx.android.synthetic.main.main_menu_include.*
import kotlinx.android.synthetic.main.main_next_course_include.*
import kotlinx.android.synthetic.main.main_weather_include.*

class Main1Fragment : BaseMainFragment<Main1Contract.Presenter>(), Main1Contract.View, View.OnClickListener {

    private var mMenuAdapter: MenuAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.main_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {

        mPresenter = Main1Presenter(this)

        btn_menu.setOnClickListener(this)
        tv_nav_about.setOnClickListener(this)
        tv_nav_share.setOnClickListener(this)
        tv_nav_setting.setOnClickListener(this)
        tv_nav_update.setOnClickListener(this)
        tv_nav_checkout.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        mPresenter.updateNextCourseView()
    }

    override fun setMenuAdapterData(menus: List<Menu>) {
        if (mMenuAdapter == null) {
            mMenuAdapter = MenuAdapter(context, menus)
            mMenuAdapter!!.setOnMenuClickListener { _, menu ->
                if (!ButtonUtils.isFastDoubleClick(Constant.ACTIVITY_MAIN)) {
                    openActivity(menu.intent)
                }
            }
            rv_menu.layoutManager = GridLayoutManager(
                    context, 4, RecyclerView.VERTICAL, false)
            rv_menu.adapter = mMenuAdapter
        } else {
            mMenuAdapter!!.setMenuList(menus)
        }
    }

    override fun setLeftMenuHeadIcon(headIcon: Drawable) {
        Glide.with(this)
                .load(headIcon)
                .into(iv_menu_icon)
    }

    override fun setLeftMenuHeadName(name: String) {
        tv_menu_name.text = name
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_menu -> drawer_main.open()
            R.id.tv_nav_update -> mPresenter.updateApp()
            R.id.tv_nav_about -> openActivity(Intent(context, AboutActivity::class.java))
            R.id.tv_nav_share -> mPresenter.shareApp()
            R.id.tv_nav_setting -> {
                startActivityForResult(Intent(context, SettingActivity::class.java), Constant.ACTIVITY_SETTING)
            }
            R.id.tv_nav_checkout -> mPresenter.checkout()

        }
        if (drawer_main.status == DragLayout.Status.Open) {
            drawer_main.close(true)
        }
    }

    override fun setWeatherText(weather: Pair<String, String>) {
        tv_weather_1.text = weather.first
        tv_weather_2.text = weather.second
    }

    override fun setCourseText(title: String, name: String, address: String, time: String) {
        tv_course_title.text = title
        tv_course_name.text = name
        tv_course_address.text = address
        tv_course_time.text = time
    }

    override fun setTimeLineData(data: List<TimeAxis>) {
        view_timeline.setTimeAxisList(data)
                .invalidate()
    }

}