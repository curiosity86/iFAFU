package cn.ifafu.ifafu.base

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelLazy
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.ViewModelFactory
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.ui.login.LoginActivity
import cn.woolsen.easymvvm.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseActivity : BaseActivity(), UIEvent {

    override suspend fun startLoginActivity() = withContext(Dispatchers.Main) {
        startActivityForResult(Intent(this@BaseActivity, LoginActivity::class.java), Constant.ACTIVITY_LOGIN)
        finish()
    }

    protected fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}