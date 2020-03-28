package cn.ifafu.ifafu.util

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cn.ifafu.ifafu.ui.elective.Elective
import cn.ifafu.ifafu.ui.elective.ElectiveView

@BindingAdapter("elective")
fun setElective(view: ElectiveView, elective: Elective?) {
    view.setElective(elective)
}

@BindingAdapter("srcUri")
fun srcUri(imageView: ImageView, uri: Uri?) {
    imageView.setImageURI(uri)
}