package cn.ifafu.ifafu.mvp.elec_login

import android.graphics.Bitmap
import android.os.Bundle
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.elec_login_activity.*

class ElecLoginActivity : BaseActivity<ElecLoginContract.Presenter>(), ElecLoginContract.View {

    private lateinit var progress: ProgressDialog

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.elec_login_activity
    }

    override fun initData(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        StatusBarUtil.setLightMode(this)

        mPresenter = ElecLoginPresenter(this)

        verifyIV.setOnClickListener { mPresenter.verify() }
        loginBtn.setOnClickListener { mPresenter.login() }

        progress = ProgressDialog(this)
        progress.setText("加载中")

    }

    override fun setSnoEtText(sno: String?) {
        accountET.setText(sno)
    }

    override fun setPasswordText(password: String?) {
        passwordET.setText(password)
    }

    override fun getSnoText(): String {
        return accountET.text.toString()
    }

    override fun getPasswordText(): String {
        return passwordET.text.toString()
    }

    override fun getVerifyText(): String {
        return verifyET.text.toString()
    }

    override fun setVerifyBitmap(bitmap: Bitmap) {
        verifyIV.setImageBitmap(bitmap)
    }

    override fun showLoading() {
        progress.show()
    }

    override fun hideLoading() {
        progress.cancel()
    }
}
