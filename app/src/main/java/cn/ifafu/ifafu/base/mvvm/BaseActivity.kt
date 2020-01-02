package cn.ifafu.ifafu.base.mvvm

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseActivity<VDB : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity(), UIEvent {

    protected open val loadingDialog by lazy {
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
        mBinding.lifecycleOwner = this
        getViewModel()?.run {
            this.event = this@BaseActivity
            mViewModel = this
        }
        initActivity(savedInstanceState)
    }

    override fun onDestroy() {
        mBinding.unbind()
        super.onDestroy()
    }

    override suspend fun showMessage(message: String) = withContext(Dispatchers.Main) {
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
    }

    override suspend fun showDialog() = withContext(Dispatchers.Main) {
        loadingDialog.show()
    }

    override suspend fun hideDialog() = withContext(Dispatchers.Main) {
        loadingDialog.cancel()
    }

    override suspend fun startLoginActivity() = withContext(Dispatchers.Main) {
        startActivityForResult(Intent(this@BaseActivity, LoginActivity::class.java), Constant.ACTIVITY_LOGIN)
        finish()
    }

    override suspend fun finishIt() = withContext(Dispatchers.IO) {
        finish()
    }

}