package cn.ifafu.ifafu.ui.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseViewModel(application) {

    val account = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val showLoading = MutableLiveData<Boolean>()
    val isLoginSuccessful = MutableLiveData<Boolean>()

    private val repo = RepositoryImpl

    fun login() = GlobalScope.launch {
        val account = checkAccountFormat(account.value) ?: return@launch
        val password = checkPasswordFormat(password.value) ?: return@launch
        showLoading.postValue(true)
        val response = repo.login(account, password)
        response.getOrFailure {
            toast(it.message ?: "登录出错")
        }?.let {
            toast("登录成功")
            isLoginSuccessful.postValue(true)
        }
        showLoading.postValue(false)
    }

    private suspend fun checkAccountFormat(account: String?): String? {
        if (account.isNullOrEmpty()) {
            toast("账号不能为空！")
            return null
        } else if (account.length != 9 && account.length != 10) {
            toast("账号格式错误！")
            return null
        }
        return account
    }

    private suspend fun checkPasswordFormat(password: String?): String? {
        if (password.isNullOrEmpty()) {
            toast("密码不能为空！")
            return null
        } else if (password.length < 6) {
            toast("密码格式错误！")
            return null
        }
        return password
    }

}