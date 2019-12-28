package cn.ifafu.ifafu.mvp.login

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.widget.addTextChangedListener
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.login_activity.*

class LoginActivity : BaseActivity<LoginContract.Presenter>(), LoginContract.View, View.OnClickListener {

    private lateinit var loadingDialog: LoadingDialog

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.login_activity
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
            Log.d(TAG, "$v, $actionId, $event")
            if (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENDCALL) {
                mPresenter.onLogin()
                GlobalLib.hideSoftKeyboard(this)
            }
            true
        }

        loadingDialog = LoadingDialog(this)
        loadingDialog.setText(R.string.logging_in)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> mPresenter.onLogin()
            R.id.btn_close -> finish()
        }
    }

    override fun showLoading() {
        loadingDialog.show()
    }

    override fun hideLoading() {
        loadingDialog.cancel()
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
