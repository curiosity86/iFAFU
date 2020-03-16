package cn.ifafu.ifafu.util

import android.app.Activity
import android.content.Context
import android.content.Intent

object ActivityUtils {
    @JvmStatic
    fun startActivity(context: Context, clazz: Class<out Activity>) {
        context.startActivity(Intent(context, clazz))
    }
}