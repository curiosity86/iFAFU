package cn.ifafu.ifafu.binding

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.ifafu.ifafu.experiment.ui.common.ElectiveView
import cn.ifafu.ifafu.experiment.vo.Elective
import cn.ifafu.ifafu.ui.main.new_theme.view.TimeEvent
import cn.ifafu.ifafu.ui.main.new_theme.view.Timeline


@BindingAdapter("timeEvents")
fun setTimeEvents(timeline: Timeline, timeEvent: List<TimeEvent>?) {
    if (timeEvent != null) {
        timeline.setTimeEvents(timeEvent)
    }
}


@BindingAdapter("elective")
fun setElective(view: ElectiveView, elective: Elective?) {
    view.setElective(elective)
}


@BindingAdapter("srcUri")
fun srcUri(imageView: ImageView, uri: Uri?) {
    imageView.setImageURI(uri)
}


@BindingAdapter("visibleGoneWhen")
fun visibleGoneWhen(view: View, gone: Boolean) {
    view.visibility = if (gone) View.GONE else View.VISIBLE
}

@BindingAdapter("layoutFullscreen")
fun layoutFullscreen(view: View, oldValue: Boolean, fullscreen: Boolean) {
//    view.setOnApplyWindowInsetsListener { v, insets ->
//        Timber.d("Status bar height: ${insets.systemWindowInsetTop}")
//        insets
//    }
//    Timber.d("Fullscreen: ${oldValue} -> ${fullscreen}")
//    if (oldValue != fullscreen) {
//        val activity = view.context as? Activity ?: return
//        if (fullscreen) {
//            view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        } else {
//        }
//    }
}

@BindingAdapter("addPaddingTopStatusBar")
fun paddingTopStatusBar(view: View, bool: Boolean) {
//    if (bool) {
//        var once = true
//        view.setOnApplyWindowInsetsListener { v, insets ->
//            if (once) {
//                val padTop = view.paddingTop + insets.systemWindowInsetTop
////                view.updateLayoutParams<ViewGroup.LayoutParams> {
////                    topMargin = view.marginTop + insets.systemWindowInsetTop
////                }
//                view.updatePadding(top = padTop)
//                once = false
//            }
//            insets
//        }
//    }
}

@BindingAdapter("hideKeyboardOnInputDone")
fun hideKeyboardOnInputDone(view: EditText, enabled: Boolean) {
    if (!enabled) return
    val listener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            view.clearFocus()
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        false
    }
    view.setOnEditorActionListener(listener)
}


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
