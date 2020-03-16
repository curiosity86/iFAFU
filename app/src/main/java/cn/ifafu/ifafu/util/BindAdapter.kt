package cn.ifafu.ifafu.util

import android.view.View
import androidx.databinding.BindingAdapter

object BindAdapter {

    @JvmStatic
    @BindingAdapter("viewGoneWhen")
    fun viewGoneWhen(view: View, show: Boolean) {
        view.visibility = if (show) { View.GONE } else { View.VISIBLE }
    }
}