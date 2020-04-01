package cn.ifafu.ifafu.view.custom

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.util.GlobalLib.getActivityFromView
import com.google.android.material.appbar.MaterialToolbar

class WoToolbar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialToolbar(context, attrs, defStyleAttr) {

    init {
        setNavigationIcon(R.drawable.ic_back)
        setNavigationOnClickListener { v ->
            val activity = getActivityFromView(v)
            activity?.finish()
        }
    }
}