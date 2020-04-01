package cn.ifafu.ifafu.binding

import androidx.annotation.LayoutRes

interface BindView {
    @LayoutRes
    fun layoutRes(): Int
    fun bindingVariable(): Int

    companion object {
        const val NONE_BINDING_VARIABLE = -1
    }
}