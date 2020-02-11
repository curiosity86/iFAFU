package cn.ifafu.ifafu.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.data.entity.GlobalSetting
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.databinding.MainActivityBinding
import cn.ifafu.ifafu.ui.login.LoginActivity
import cn.ifafu.ifafu.ui.main.fragment.MainNewFragment
import cn.ifafu.ifafu.ui.main.fragment.MainOldFragment
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.view.adapter.AccountAdapter
import cn.ifafu.ifafu.view.custom.DragLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.account_dialog.view.*
import kotlinx.android.synthetic.main.main_new_fragment.*
import kotlinx.android.synthetic.main.main_old_fragment.*

//TODO 修改主题自动切换
class MainActivity : BaseActivity<MainActivityBinding, MainViewModel>() {

    private var nowTheme = -999

    private val mAccountAdapter: AccountAdapter by lazy {
        AccountAdapter {
            showAccountDetailDialog(it)
        }
    }

    private val switchAccountDialog: MaterialDialog by lazy {
        MaterialDialog(this).apply {
            customView(viewRes = R.layout.account_dialog)
            val rvAccount = getCustomView().rv_account
            rvAccount.adapter = mAccountAdapter
            rvAccount.layoutManager = LinearLayoutManager(this@MainActivity)
            title(text = "多账号管理")
            negativeButton(text = "添加账号") {
                val intent = Intent(context, LoginActivity::class.java)
                intent.putExtra("from", Constant.ACTIVITY_MAIN)
                startActivityForResult(intent, Constant.ACTIVITY_LOGIN)
            }
            if (BuildConfig.DEBUG) {
                neutralButton(text = "导入账号") {
                    mViewModel.importAccount()
                }
            }
        }
    }

    override fun getViewModel(): MainViewModel {
        return VMProvider(this)[MainViewModel::class.java]
    }

    override fun getLayoutId(): Int = R.layout.main_activity

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).init()
        mViewModel.theme.observe(this, Observer {
            Log.d("THEME", "theme: $it")
            supportFragmentManager.beginTransaction().apply {
                when (it) {
                    GlobalSetting.THEME_NEW -> replace(R.id.view_content, MainNewFragment())
                    GlobalSetting.THEME_OLD -> replace(R.id.view_content, MainOldFragment())
                }
            }.commitNow()
        })
        mViewModel.users.observe(this, Observer {
            mAccountAdapter.replaceData(it)
        })
        mViewModel.isShowSwitchAccountDialog.observe(this, Observer {
            if (it) {
                switchAccountDialog.show()
            } else {
                switchAccountDialog.cancel()
            }
        })
        mViewModel.initActivityData()
    }

    private fun showAccountDetailDialog(user: User) {
        MaterialDialog(this).show {
            title(text = "${user.name} ${user.account}")
            customView(viewRes = R.layout.account_password_dialog)
            getCustomView().findViewById<EditText>(R.id.et_password).setText(user.password)
            negativeButton(text = "删除账号") {
                mViewModel.deleteUser(user)
            }
            positiveButton(text = "切换账号") {
                mViewModel.checkoutTo(user)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.ACTIVITY_LOGIN) {
                mViewModel.addAccountSuccess()
            } else if (requestCode == Constant.ACTIVITY_SETTING) {
                mViewModel.checkTheme()
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        println("request code: ${requestCode}, result code: ${resultCode}")
    }
}