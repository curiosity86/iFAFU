package cn.ifafu.ifafu.mvp.main

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.view.GravityCompat
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.databinding.MainActivityBinding
import cn.ifafu.ifafu.entity.GlobalSetting
import cn.ifafu.ifafu.mvp.main.new.Main1Fragment
import cn.ifafu.ifafu.mvp.main.old.MainOldFragment
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.view.custom.DragLayout
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_old_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity<MainActivityBinding, BaseViewModel>() {

    private var nowTheme = -999

    override fun getViewModel(): BaseViewModel? = null

    override fun getLayoutId(): Int = R.layout.main_activity

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).init()
        checkoutFragment()
    }

    private fun checkoutFragment() {
        GlobalScope.launch {
            val setting = Repository.getGlobalSetting()
            nowTheme = setting.theme
            withContext(Dispatchers.Main) {
                supportFragmentManager.beginTransaction().apply {
                    when (setting.theme) {
                        GlobalSetting.THEME_NEW -> replace(R.id.view_content, Main1Fragment())
                        GlobalSetting.THEME_OLD -> replace(R.id.view_content, MainOldFragment())
                    }
                }.commitAllowingStateLoss()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
                if (nowTheme == GlobalSetting.THEME_NEW && drawer_main.status == DragLayout.Status.Open) {
                    drawer_main.close(true)
                } else if (nowTheme == GlobalSetting.THEME_OLD && layout_drawer.isDrawerOpen(GravityCompat.START)) {
                    layout_drawer.closeDrawer(GravityCompat.START)
                } else if (ButtonUtils.isFastDoubleClick()) {
                    finish()
                } else {
                    Toast.makeText(this, R.string.back_again, Toast.LENGTH_SHORT).show()
                }
                return true
            }
            return super.onKeyDown(keyCode, event)
        } catch (e: Exception) {
            e.printStackTrace()
            return super.onKeyDown(keyCode, event)
        }
    }

}