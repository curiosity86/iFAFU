package cn.ifafu.ifafu.mvp.login

import android.app.Application
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : BaseViewModel(application) {

    fun login(account: String, password: String, success: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            if (!checkFormatIsOK(account, password)) {
                return@launch
            }
            event.showDialog()
            val account2 = if (account.length == 10 && account.getOrNull(0) == '0') {
                account.drop(1)
            } else {
                account
            }
            try {
                val response = mRepository.login(account2, password)
                if (response.isSuccess) {
                    val user = User().apply {
                        this.account = if (account.getOrNull(0) == '0') account.drop(1) else account
                        this.password = password
                        if (account.length == 9) {
                            this.schoolCode = School.FAFU_JS
                        } else if (account.length == 10) {
                            this.schoolCode = School.FAFU
                        }
                    }
                    mRepository.saveUser(user)
                    mRepository.saveLoginUser(user)
                    success()
                } else {
                    event.showMessage(response.message)
                }
            } catch (e: Exception) {
                event.showMessage(e.errorMessage())
            }
            event.hideDialog()
        }
    }

    private suspend fun checkFormatIsOK(account: String, password: String): Boolean {
        return when {
            account.isEmpty() -> {
                event.showMessage("账号不能为空！")
                false
            }
            account.length != 9 && account.length != 10 -> {
                event.showMessage("账号格式错误！")
                false
            }
            password.isEmpty() -> {
                event.showMessage("密码不能为空！")
                false
            }
            password.length < 6 -> {
                event.showMessage("密码格式错误！")
                false
            }
            else -> {
                true
            }
        }
    }

}