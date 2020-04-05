package cn.ifafu.ifafu.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityLoginBinding
import cn.ifafu.ifafu.ui.main.MainActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog
import kotlinx.android.synthetic.main.activity_login.*

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
            closable = fromActivity == Constant.ACTIVITY_MAIN
            vm = viewModel
        }

        //初始化监听事件
        btn_close.setOnClickListener {
            finish()
        }
        et_password.setOnEditorActionListener { v, actionId, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                viewModel.login()
            }
            true
        }

        //初始化ViewModel
        viewModel.showLoading.observe(this, Observer {
            with(mLoadingDialog) {
                if (it) show() else cancel()
            }
        })
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
