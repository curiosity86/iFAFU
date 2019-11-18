package cn.ifafu.ifafu.mvp.main

import android.os.Bundle
import android.view.KeyEvent
import androidx.core.view.GravityCompat
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.data.entity.Setting
import cn.ifafu.ifafu.data.local.RepositoryImpl
import cn.ifafu.ifafu.mvp.main.main1.Main1Fragment
import cn.ifafu.ifafu.mvp.main.main2.Main2Fragment
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.view.custom.DragLayout
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_main1.*
import kotlinx.android.synthetic.main.fragment_main2.*

class MainActivity : BaseActivity<IPresenter>() {

    private var nowTheme = -999

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).init()
        checkoutFragment()
    }

    private fun checkoutFragment() {
        RepositoryImpl.getInstance().setting.run {
//            MobclickAgent.onProfileSignIn(account)
            if (nowTheme != theme) {
                nowTheme = theme
                supportFragmentManager.beginTransaction().apply {
                    if (theme == Setting.THEME_NEW) {
                        replace(R.id.view_content, Main1Fragment())
                    } else {
                        replace(R.id.view_content, Main2Fragment())
                    }
                }.commit()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (nowTheme == Setting.THEME_NEW && drawer_main.status == DragLayout.Status.Open) {
                drawer_main.close(true)
            } else if (nowTheme == Setting.THEME_OLD && layout_drawer.isDrawerOpen(GravityCompat.START)) {
                layout_drawer.closeDrawer(GravityCompat.START)
            } else if (ButtonUtils.isFastDoubleClick()) {
                finish()
            } else {
                showMessage(R.string.back_again)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}