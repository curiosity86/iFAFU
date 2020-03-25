package cn.ifafu.ifafu.util

import androidx.databinding.BindingAdapter
import cn.ifafu.ifafu.ui.elective.Elective
import cn.ifafu.ifafu.ui.elective.ElectiveView

@BindingAdapter("elective")
fun setElective(view: ElectiveView, elective: Elective?) {
    view.setElective(elective)
}