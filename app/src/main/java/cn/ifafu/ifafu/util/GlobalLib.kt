package cn.ifafu.ifafu.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager

object GlobalLib {
    fun trimZero(s: String): String {
        var s = s
        if (s.indexOf(".") > 0) { // 去掉多余的0
            s = s.replace("0+?$".toRegex(), "")
            // 如最后一位是.则去掉
            s = s.replace("[.]$".toRegex(), "")
        }
        return s
    }

    fun formatFloat(num: Float, digit: Int): String {
        return trimZero(String.format("%." + digit + "f", num))
    }

    @JvmStatic
    fun getActivityFromView(view: View?): Activity? {
        if (null != view) {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) {
                    return context
                }
                context = context.baseContext
            }
        }
        return null
    }

}