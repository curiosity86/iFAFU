package cn.ifafu.ifafu.mvp.login

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.widget.addTextChangedListener
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<LoginContract.Presenter>(), LoginContract.View, View.OnClickListener {

    private val progressDialog: ProgressDialog by lazy { ProgressDialog(this) }

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
        btn_login.setOnClickListener(this)
        et_account.addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    mPresenter.checkAccount(s.toString())
                }
        )
        et_password.setOnEditorActionListener { v, actionId, event ->
            if (actionId == KeyEvent.ACTION_DOWN) {
                mPresenter.onLogin()
            }
            true
        }

        progressDialog.setText(R.string.logging_in)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> mPresenter.onLogin()
            R.id.btn_close -> finish()
        }
    }

    override fun showLoading() {
        progressDialog.show()
    }

    override fun hideLoading() {
        progressDialog.cancel()
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
