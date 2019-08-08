package cn.ifafu.ifafu.mvp.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.GravityCompat
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.Menu
import cn.ifafu.ifafu.data.Weather
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.mvp.other.AboutActivity
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.view.adapter.MenuAdapter
import cn.ifafu.ifafu.view.listener.ZoomDrawerListener
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_left_menu.*
import kotlinx.android.synthetic.main.include_left_menu.view.*
import kotlinx.android.synthetic.main.include_weather.*

class MainActivity : BaseActivity<MainContract.Presenter>(), MainContract.View, View.OnClickListener {

    private val mMenuAdapter: MenuAdapter by lazy { MenuAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImmersionBar.with(this).init()

        mPresenter = MainPresenter(this)

        btn_menu.setOnClickListener(this)
        initNavigationView()

        mPresenter.onStart()
    }

    //初始化侧滑栏样式
    private fun initNavigationView() {
        drawer_main.setScrimColor(Color.TRANSPARENT)
        drawer_main.addDrawerListener(ZoomDrawerListener(this, layout_content, left_menu_main))
        tv_nav_about.setOnClickListener(this)
        tv_nav_share.setOnClickListener(this)
        tv_nav_fback.setOnClickListener(this)
        tv_nav_update.setOnClickListener(this)
        tv_nav_logout.setOnClickListener(this)
    }

    override fun setMenuAdapterData(menus: List<Menu>) {
        mMenuAdapter.setMenuList(menus)
        mMenuAdapter.notifyDataSetChanged()
    }

    override fun setLeftMenuHeadIcon(headIcon: Drawable) {
        Glide.with(this)
                .load(headIcon)
                .into(left_menu_main.iv_menu_icon)
    }

    override fun setLeftMenuHeadName(name: String) {
        left_menu_main.tv_menu_name.text = name
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            when {
                drawer_main.isDrawerOpen(GravityCompat.START) -> drawer_main.closeDrawers()
                ButtonUtils.isFastDoubleClick() -> finish()
                else -> showMessage(R.string.back_again)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_menu -> drawer_main.openDrawer(GravityCompat.START)
            R.id.tv_nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.tv_nav_share -> mPresenter.shareApp()
            R.id.tv_nav_fback -> showMessage("反馈问题")
            R.id.tv_nav_update -> showMessage("检查更新")
            R.id.tv_nav_logout -> mPresenter.quitAccount()
        }
    }

    override fun setWeatherText(weather: Weather) {
        tv_weather_1.text = (weather.nowTemp.toString() + "℃")
        tv_weather_2.text = String.format("%s | %s", weather.cityName, weather.weather)
    }
}
