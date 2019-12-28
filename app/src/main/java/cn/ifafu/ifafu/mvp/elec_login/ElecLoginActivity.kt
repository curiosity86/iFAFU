package cn.ifafu.ifafu.mvp.elec_login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.ViewModelFactory
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.ElecLoginActivityBinding
import cn.ifafu.ifafu.mvp.elec_main.ElecMainActivity
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import com.jaeger.library.StatusBarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElecLoginActivity : BaseActivity<ElecLoginActivityBinding>() {

    private val progressDialog by lazy {
        LoadingDialog(this).apply { setText("登录中") }
    }

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory)
                .get(ElecLoginViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.elec_login_activity

    override fun initActivity(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        StatusBarUtil.setLightMode(this)
        viewModel.init { account -> mBinding.account = account }
        refreshVerify()
        mBinding.verifyIV.setOnClickListener {
            refreshVerify()
        }
        mBinding.loginBtn.setOnClickListener {
            progressDialog.show()
            viewModel.login(
                    account = mBinding.account ?: "",
                    password = mBinding.password ?: "",
                    verify = mBinding.verify ?: "",
                    success = {
                        withContext(Dispatchers.Main) {
                            progressDialog.show()
                            startActivity(Intent(this@ElecLoginActivity, ElecMainActivity::class.java))
                            finish()
                        }
                    },
                    fail = {
                        withContext(Dispatchers.Main) {
                            progressDialog.show()
                            showMessage(it)
                            refreshVerify()
                        }
                    }
            )
        }
    }

    private fun refreshVerify() {
        viewModel.refreshVerify({
            withContext(Dispatchers.Main) {
                mBinding.verifyIV.setImageBitmap(it)
            }
        }, this::showMessage)
    }
}
