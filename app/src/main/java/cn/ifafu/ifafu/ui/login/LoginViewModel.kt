package cn.ifafu.ifafu.ui.login

import android.app.Application
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.Repository
import cn.woolsen.easymvvm.livedata.LiveDataBoolean
import cn.woolsen.easymvvm.livedata.LiveDataString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseViewModel(application) {

    val account = LiveDataString()
    val password = LiveDataString()
    val toastMessage = LiveDataString()
    val showLoading = LiveDataBoolean()
    val isLoginSuccessful = LiveDataBoolean()

    fun login() = GlobalScope.launch {
        val account = checkAccountFormat(account.value) ?: return@launch
        val password = checkPasswordFormat(password.value) ?: return@launch
        showLoading.postValue(true)
        try {
            val response = Repository.user.login2(account, password)
            val user = response.getOrFailure {
                toastMessage.postValue(it.message)
            } ?: return@launch
            Repository.user.saveLoginOnly(user)
            Repository.user.save(user)
            isLoginSuccessful.postValue(true)
        } catch (e: Exception) {
            toastMessage.postValue(e.errorMessage())
        }
        showLoading.postValue(false)
    }

    private fun checkAccountFormat(account: String?): String? {
        if (account.isNullOrEmpty()) {
            toastMessage.postValue("账号不能为空！")
            return null
        } else if (account.length != 9 && account.length != 10) {
            toastMessage.postValue("账号格式错误！")
            return null
        }
        return account
    }

    private fun checkPasswordFormat(password: String?): String? {
        if (password.isNullOrEmpty()) {
            toastMessage.postValue("密码不能为空！")
            return null
        } else if (password.length < 6) {
            toastMessage.postValue("密码格式错误！")
            return null
        }
        return password
    }

}