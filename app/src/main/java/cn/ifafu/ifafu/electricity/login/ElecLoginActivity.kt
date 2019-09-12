package cn.ifafu.ifafu.electricity.login

import android.graphics.Bitmap
import android.os.Bundle
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.util.SPUtils
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_elec_login.*

class ElecLoginActivity : BaseActivity<ElecLoginContract.Presenter>(), ElecLoginContract.View {

    private lateinit var progress: ProgressDialog

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_elec_login
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

    override fun setSnoEtText(sno: String) {
        accountET.setText(sno)
    }

    override fun setPasswordText(password: String) {
        passwordET.setText(password)
    }

    override fun getSNoEditable(): String {
        return accountET.text.toString()
    }

    override fun getPasswordEditable(): String {
        return passwordET.text.toString()
    }

    override fun getVerifyEditable(): String {
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
