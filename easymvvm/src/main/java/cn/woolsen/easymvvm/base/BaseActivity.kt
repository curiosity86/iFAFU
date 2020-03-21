package cn.woolsen.easymvvm.base

import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity : AppCompatActivity() {

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

    fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}