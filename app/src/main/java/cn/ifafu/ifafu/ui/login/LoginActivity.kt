package cn.ifafu.ifafu.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.databinding.ActivityLoginBinding
import cn.ifafu.ifafu.ui.main.MainActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.woolsen.easymvvm.base.BaseActivity
import cn.woolsen.easymvvm.util.ToastUtils

class LoginActivity : BaseActivity() {

    private var fromActivity = 0

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("登录中")
            setCancelable(true)
        }
    }

    private val viewModel by viewModels<LoginViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        fromActivity = intent.getIntExtra("from", 0)
        with(bind<ActivityLoginBinding>(R.layout.activity_login)) {
            //LoginActivity是否可以关闭
            if (fromActivity != Constant.ACTIVITY_SPLASH) {
                btnClose.setOnClickListener {
                    finish()
                }
            } else {
                btnClose.visibility = View.GONE
            }
            vm = viewModel
        }
        viewModel.toastMessage.observe(this, Observer {
            ToastUtils.showToastShort(this, it)
        })
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
