package cn.ifafu.ifafu.mvp.login

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.DrawableRes
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<LoginContract.Presenter>(), LoginContract.View, View.OnClickListener {

    private var progressDialog: ProgressDialog? = null

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_login
    }

    override fun initData(savedInstanceState: Bundle?) {
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setTransparent(this)

        mPresenter = LoginPresenter(this)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            bg_logo.visibility = View.GONE
        }

        btn_close.setOnClickListener(this)
        et_account.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mPresenter.checkAccount(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        btn_login.setOnClickListener(this)

        progressDialog = ProgressDialog(this)
        progressDialog!!.setText(R.string.logging_in)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> mPresenter.onLogin()
            R.id.btn_close -> finish()
        }
    }

    override fun showLoading() {
        progressDialog!!.show()
    }

    override fun hideLoading() {
        progressDialog!!.cancel()
    }

    override fun getAccountText(): String {
        return et_account.text.toString()
    }

    override fun getPasswordText(): String {
        return et_password.text.toString()
    }

    override fun showCloseBtn() {
        btn_close.visibility = View.VISIBLE
    }

    override fun setBackgroundLogo(@DrawableRes resId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Glide.with(this)
                    .load(resId)
                    .into(bg_logo)
        }
    }

}
