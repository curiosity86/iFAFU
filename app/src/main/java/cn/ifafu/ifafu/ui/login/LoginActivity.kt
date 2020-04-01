package cn.ifafu.ifafu.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityLoginBinding
import cn.ifafu.ifafu.ui.main.MainActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog

class LoginActivity : BaseActivity() {

    private var fromActivity = 0

    private val mLoadingDialog = LoadingDialog(this, "登录中")

    private val viewModel by viewModels<LoginViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        fromActivity = intent.getIntExtra("from", 0)
        with(bind<ActivityLoginBinding>(R.layout.activity_login)) {
            //LoginActivity是否可以关闭
            if (fromActivity == Constant.ACTIVITY_MAIN) {
                btnClose.setOnClickListener {
                    finish()
                }
            } else {
                btnClose.visibility = View.GONE
            }
            vm = viewModel
            etPassword.setOnEditorActionListener { v, actionId, event ->
                if (actionId == KeyEvent.ACTION_DOWN) {
                    viewModel.login()
                }
                true
            }
        }
        viewModel.showLoading.observe(this, Observer {
            with(mLoadingDialog) {
                if (it) show() else cancel()
            }
        })
        //登录成功回调
        viewModel.isLoginSuccessful.observe(this, Observer {
            when (fromActivity) {
                0, Constant.ACTIVITY_SPLASH -> {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                    setResult(Activity.RESULT_OK)
                }
            }
            finish()
        })
    }


}
