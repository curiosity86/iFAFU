package cn.ifafu.ifafu.binding

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

@BindingAdapter("bindViews")
fun bindView(layout: ViewGroup, bindViews: BindViews?) {
    if (bindViews == null) return
    layout.removeAllViews()
    val inflater = LayoutInflater.from(layout.context)
    for (bindData in bindViews) {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(inflater, bindData.layoutRes(), layout, true)
        if (bindData.bindingVariable() != BindView.NONE_BINDING_VARIABLE) {
            binding.setVariable(bindData.bindingVariable(), bindData)
        }
    }
}