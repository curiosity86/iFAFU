package cn.ifafu.ifafu.base

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.ui.login.LoginActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.woolsen.easymvvm.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseActivity<VDB : ViewDataBinding, VM : BaseViewModel> : BaseActivity(), UIEvent {

    protected open val mLoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("加载中")
            setCancelable(true)
        }
    }

    protected val mBinding: VDB by lazy {
        DataBindingUtil.setContentView(this, getLayoutId()) as VDB
    }

    protected lateinit var mViewModel: VM

    protected abstract fun getLayoutId(): Int

    protected abstract fun getViewModel(): VM?

    protected abstract fun initActivity(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        getViewModel()?.run {
            this.event = this@BaseActivity
            mViewModel = this
        }
        mBinding.lifecycleOwner = this
        initActivity(savedInstanceState)
    }

    override fun onDestroy() {
        mBinding.unbind()
        super.onDestroy()
    }

    override suspend fun showMessage(message: String) = withContext(Dispatchers.Main) {
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
    }

    override suspend fun showMessage(msgId: Int) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@BaseActivity, msgId, Toast.LENGTH_SHORT).show()
        }
    }

    override suspend fun showDialog() = withContext(Dispatchers.Main) {
        mLoadingDialog.show()
    }

    override suspend fun hideDialog() = withContext(Dispatchers.Main) {
        mLoadingDialog.cancel()
    }

    override suspend fun startLoginActivity() = withContext(Dispatchers.Main) {
        startActivityForResult(Intent(this@BaseActivity, LoginActivity::class.java), Constant.ACTIVITY_LOGIN)
        finish()
    }

    override suspend fun finishIt() = withContext(Dispatchers.Main) {
        finish()
    }

}