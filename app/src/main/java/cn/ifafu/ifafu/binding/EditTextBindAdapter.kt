package cn.ifafu.ifafu.binding

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Hides keyboard when the [EditText] is focused.
 *
 * Note that there can only be one [TextView.OnEditorActionListener] on each [EditText] and
 * this [BindingAdapter] sets it.
 */
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