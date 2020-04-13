package cn.ifafu.ifafu.experiment.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseSimpleActivity
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.databinding.ActivityLoginBinding
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.ui.main.MainActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseSimpleActivity() {

    private var fromActivity = 0

    private val mLoadingDialog = LoadingDialog(this, "登录中")

    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fromActivity = intent.getIntExtra("from", 0)
        DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login).apply {
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
        viewModel.loginStatus.observe(this, Observer { res ->
            when (res) {
                is Resource.Success -> {
                    mLoadingDialog.cancel()
                    setResult(Activity.RESULT_OK)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Resource.Loading -> {
                    mLoadingDialog.show("登录中")
                }
                is Resource.Error -> {
                    mLoadingDialog.cancel()
                    toast(res.message)
                }
            }
        })
    }


}
