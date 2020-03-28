package cn.ifafu.ifafu.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.ui.login.LoginActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.woolsen.easymvvm.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseFragment : BaseFragment(), UIEvent {

    override suspend fun startLoginActivity() {
        withContext(Dispatchers.Main) {
            startActivityForResult(Intent(this@BaseFragment.requireContext(), LoginActivity::class.java), Constant.ACTIVITY_LOGIN)
            activity?.finish()
        }
    }
}