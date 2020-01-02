package cn.ifafu.ifafu.mvp.elec_login

import android.content.Intent
import android.os.Bundle
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.ViewModelProvider
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.ElecLoginActivityBinding
import cn.ifafu.ifafu.mvp.elec_main.ElecMainActivity
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import com.jaeger.library.StatusBarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElecLoginActivity : BaseActivity<ElecLoginActivityBinding, ElecLoginViewModel>() {

    override val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("登录中")
            setCancelable(true)
        }
    }

    override fun getViewModel(): ElecLoginViewModel =
            ViewModelProvider(this).get(ElecLoginViewModel::class.java)

    override fun getLayoutId(): Int = R.layout.elec_login_activity

    override fun initActivity(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        StatusBarUtil.setLightMode(this)
        mViewModel.init { account -> mBinding.account = account }
        refreshVerify()
        mBinding.verifyIV.setOnClickListener {
            refreshVerify()
        }
        mBinding.loginBtn.setOnClickListener {
            mViewModel.login(
                    account = mBinding.account ?: "",
                    password = mBinding.password ?: "",
                    verify = mBinding.verify ?: "",
                    success = {
                        withContext(Dispatchers.Main) {
                            startActivity(Intent(this@ElecLoginActivity, ElecMainActivity::class.java))
                            finish()
                        }
                    }
            )
        }
    }

    private fun refreshVerify() {
        mViewModel.refreshVerify {
            withContext(Dispatchers.Main) {
                mBinding.verifyIV.setImageBitmap(it)
            }
        }
    }
}
