package cn.ifafu.ifafu.mvp.login

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.widget.addTextChangedListener
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.ViewModelProvider
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.LoginActivityBinding
import cn.ifafu.ifafu.mvp.main.MainActivity
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import com.bumptech.glide.Glide
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginActivity : BaseActivity<LoginActivityBinding, LoginViewModel>(), View.OnClickListener {

    private val startByWhichActivity by lazy {
        intent.getIntExtra("from", 0)
    }

    override val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("登录中")
            setCancelable(true)
        }
    }

    override fun getLayoutId(): Int = R.layout.login_activity

    override fun getViewModel(): LoginViewModel {
        return ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        StatusBarUtil.setLightMode(this)
        StatusBarUtil.setTransparent(this)
        btn_close.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        et_account.addTextChangedListener(
                onTextChanged = { account, _, _, _ ->
                    if (account.isNullOrEmpty() || account.length < 9) return@addTextChangedListener
                    if (account[0] == '0' || account.length == 9) {
                        setBackgroundLogo(R.drawable.fafu_js_icon)
                    } else {
                        setBackgroundLogo(R.drawable.fafu_bb_icon)
                    }
                }
        )
        et_password.setOnEditorActionListener { v, actionId, event ->
            if (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENDCALL) {
                GlobalLib.hideSoftKeyboard(this)
                login()
            }
            true
        }
        if (startByWhichActivity == Constant.ACTIVITY_SPLASH) {
            btn_close.visibility = View.GONE
        } else {
            btn_close.visibility = View.VISIBLE
        }
    }

    private fun login() {
        mViewModel.login(mBinding.account ?: "", mBinding.password ?: "") {
            withContext(Dispatchers.Main) {
                when (startByWhichActivity) {
                    0 -> {
                        println("登录成功")
                        showMessage("登录成功")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        println("结束")
                        setResult(Activity.RESULT_OK)
                    }
                }
                finish()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> login()
            R.id.btn_close -> finish()
        }
    }

    private fun setBackgroundLogo(@DrawableRes resId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Glide.with(this)
                    .load(resId)
                    .into(bg_logo)
        }
    }


}
