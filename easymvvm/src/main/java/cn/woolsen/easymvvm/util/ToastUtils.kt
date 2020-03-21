package cn.woolsen.easymvvm.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * create by woolsen on 19/7/12
 */
object ToastUtils {
    fun showToastLong(context: Context, text: CharSequence) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    fun showToastLong(context: Context, @StringRes resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }

    fun showToastShort(context: Context, text: CharSequence) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun showToastShort(context: Context, @StringRes resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }
}