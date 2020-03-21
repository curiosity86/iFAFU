package cn.woolsen.easymvvm.binding

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.util.*

@BindingAdapter("bindViews")
fun bindView(layout: ViewGroup, bindViews: BindViews?) {
    Log.d("BindView", "bindView")
    if (bindViews == null) return
    layout.removeAllViews()
    val inflater = LayoutInflater.from(layout.context)
    for (bindData in bindViews) {
        Log.d("BindView", "variable: " + bindData.bindingVariable())
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(inflater, bindData.layoutRes(), layout, true)
        if (bindData.bindingVariable() != BindView.NONE_BINDING_VARIABLE) {
            binding.setVariable(bindData.bindingVariable(), bindData)
        }
    }
}