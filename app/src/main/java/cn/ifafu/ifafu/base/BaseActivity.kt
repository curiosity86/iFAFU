package cn.ifafu.ifafu.base

import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.experiment.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseActivity : AppCompatActivity(), UIEvent {

    fun <VDB : ViewDataBinding> bind(@LayoutRes layoutRes: Int): VDB {
        return DataBindingUtil.setContentView<VDB>(this, layoutRes).apply {
            lifecycleOwner = this@BaseActivity
        }
    }

    /**
     * 设置亮色状态栏（黑色图标）
     */
    fun setLightUiBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override suspend fun startLoginActivity() = withContext(Dispatchers.Main) {
        startActivityForResult(Intent(this@BaseActivity, LoginActivity::class.java), Constant.ACTIVITY_LOGIN)
        finish()
    }

    protected fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}