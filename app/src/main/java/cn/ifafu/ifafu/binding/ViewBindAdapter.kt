package cn.ifafu.ifafu.binding

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("onClickCommand")
fun onClickCommand(view: View, command: OnClickCommand? = null) {
    view.setOnClickListener {
        command?.onClick(it)
    }
}

@BindingAdapter("visibleGoneWhen")
fun visibleGoneWhen(view: View, gone: Boolean) {
    view.visibility = if (gone) View.GONE else View.VISIBLE
}

