package cn.ifafu.ifafu.base

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class BaseSimpleFragment : Fragment() {

    protected open fun toast(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    protected fun startActivityByClazz(clazz: Class<out Activity>,
                                       applyIntent: ((Intent) -> Intent)? = null) {
        val intent = Intent(requireContext(), clazz)
        if (applyIntent != null) {
            applyIntent(intent)
        }
        startActivity(intent)
    }

}