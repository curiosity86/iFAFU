package cn.ifafu.ifafu.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.GlobalSetting
import cn.ifafu.ifafu.ui.login.LoginActivity
import cn.ifafu.ifafu.ui.main.new_theme.MainNewFragment
import cn.ifafu.ifafu.ui.main.old_theme.MainOldFragment
import cn.ifafu.ifafu.ui.main.view.MultiUserDialog
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.util.ButtonUtils
import cn.ifafu.ifafu.ui.view.custom.DragLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_main_new.*
import kotlinx.android.synthetic.main.fragment_main_old.*

class MainActivity : BaseActivity() {

    private var nowTheme = -999

    private val mMultiUserDialog by lazy {
        MultiUserDialog(this, {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("from", Constant.ACTIVITY_MAIN)
            startActivityForResult(intent, Constant.ACTIVITY_LOGIN)
        }, { user ->
            MaterialDialog(this).show {
                title(text = "${user.name} ${user.account}")
                customView(viewRes = R.layout.dialog_user_detail)
                getCustomView().findViewById<EditText>(R.id.et_password).setText(user.password)
                negativeButton(text = "删除账号") {
                    mViewModel.deleteUser(user)
                }
                positiveButton(text = "切换账号") {
                    mViewModel.checkoutTo(user)
                }
            }
        })
    }

    private val loadingDialog = LoadingDialog(this)

    private val mViewModel: MainViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewModel.theme.observe(this, Observer {
            supportFragmentManager.beginTransaction().apply {
                when (it) {
                    GlobalSetting.THEME_NEW -> replace(R.id.view_content, MainNewFragment())
                    GlobalSetting.THEME_OLD -> replace(R.id.view_content, MainOldFragment())
                }
            }.commitNowAllowingStateLoss()
        })
        mViewModel.users.observe(this, Observer {
            mMultiUserDialog.setUsers(it)
        })
        mViewModel.startLoginActivity.observe(this, Observer {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })
        mViewModel.showMultiUserDialog.observe(this, Observer {
            with(mMultiUserDialog) {
                if (it) show() else cancel()
            }
        })
        loadingDialog.observe(this, mViewModel.loading)
        mViewModel.initActivityData()
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
            when (requestCode) {
                Constant.ACTIVITY_LOGIN -> {
                    mViewModel.addAccountSuccess()
                }
                Constant.ACTIVITY_SETTING -> {
                    mViewModel.initActivityData()
                }
                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}