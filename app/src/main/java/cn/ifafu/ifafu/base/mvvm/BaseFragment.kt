package cn.ifafu.ifafu.base.mvvm

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
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseFragment<VDB : ViewDataBinding, VM : BaseViewModel> : Fragment(), UIEvent {

    protected open val loadingDialog by lazy {
        LoadingDialog(requireContext()).apply {
            setText("加载中")
            setCancelable(true)
        }
    }

    protected lateinit var mBinding: VDB
    protected lateinit var mViewModel: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.lifecycleOwner = this
        mViewModel = getViewModel()
        mViewModel.event = this
        initFragment(savedInstanceState)
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun getViewModel(): VM

    protected abstract fun initFragment(savedInstanceState: Bundle?)

    override suspend fun showMessage(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@BaseFragment.requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override suspend fun showMessage(msgId: Int) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@BaseFragment.requireContext(), msgId, Toast.LENGTH_SHORT).show()
        }
    }

    override suspend fun showDialog() {
        withContext(Dispatchers.Main) {
            loadingDialog.show()
        }
    }

    override suspend fun hideDialog() {
        withContext(Dispatchers.Main) {
            loadingDialog.cancel()
        }
    }

    override suspend fun startLoginActivity() {
        withContext(Dispatchers.Main) {
            startActivityForResult(Intent(this@BaseFragment.requireContext(), LoginActivity::class.java), Constant.ACTIVITY_LOGIN)
            activity?.finish()
        }
    }

    override suspend fun finishIt() {
        withContext(Dispatchers.IO) {
            activity?.finish()
        }
    }
}