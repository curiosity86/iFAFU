package cn.ifafu.ifafu.base.mvvm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseActivity<VDB: ViewDataBinding>: AppCompatActivity() {

    protected val mBinding: VDB by lazy {
        DataBindingUtil.setContentView(this, getLayoutId()) as VDB
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.lifecycleOwner = this
        initActivity(savedInstanceState)
    }

    override fun onDestroy() {
        mBinding.unbind()
        super.onDestroy()
    }

    abstract fun getLayoutId(): Int

    abstract fun initActivity(savedInstanceState: Bundle?)

    protected suspend fun showMessage(message: String) = withContext(Dispatchers.Main) {
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
    }

}