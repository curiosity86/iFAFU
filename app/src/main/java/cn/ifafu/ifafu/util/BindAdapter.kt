package cn.ifafu.ifafu.util

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cn.ifafu.ifafu.ui.elective.Elective
import cn.ifafu.ifafu.ui.elective.ElectiveView
import cn.ifafu.ifafu.view.custom.SmoothCheckBox

@BindingAdapter("elective")
fun setElective(view: ElectiveView, elective: Elective?) {
    view.setElective(elective)
}

@BindingAdapter("srcUri")
fun srcUri(imageView: ImageView, uri: Uri?) {
    imageView.setImageURI(uri)
}

@BindingAdapter("checkedWithoutAnim")
fun setCheckWithoutAnim(checkBox: SmoothCheckBox, checked: Boolean?) {
    checkBox.setChecked(checked ?: false, false)
}

@BindingAdapter("checkedWithAnim")
fun setCheckWithAnim(checkBox: SmoothCheckBox, checked: Boolean?) {
    checkBox.setChecked(checked ?: false, true)
}