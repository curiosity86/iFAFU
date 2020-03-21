package cn.woolsen.easymvvm.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    private var rootView: View? = null

    @LayoutRes
    protected abstract fun layoutRes(): Int

    protected abstract fun afterOnActivityCreated(savedInstanceState: Bundle?)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        /* 在ViewPager中随着页面滑动这个方法会调用多次，inflate过了之后就直接用 */
        if (rootView == null) {
            rootView = inflater.inflate(layoutRes(), container, false)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        afterOnActivityCreated(savedInstanceState)
    }

    protected fun <VDB : ViewDataBinding> bind(): VDB {
        return (DataBindingUtil.bind<VDB>(
                rootView ?: throw IllegalAccessException("rootView is null")
        ) ?: throw IllegalAccessException("can't bind view")).apply {
            lifecycleOwner = this@BaseFragment
        }
    }
}