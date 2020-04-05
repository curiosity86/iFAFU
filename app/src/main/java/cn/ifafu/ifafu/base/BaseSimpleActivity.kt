package cn.ifafu.ifafu.base

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseSimpleActivity : AppCompatActivity() {

    protected open fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun startActivityByClazz(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }
}