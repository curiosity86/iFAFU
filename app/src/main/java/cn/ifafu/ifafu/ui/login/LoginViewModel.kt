package cn.ifafu.ifafu.ui.login

import android.app.Application
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.data.entity.User

class LoginViewModel(application: Application) : BaseViewModel(application) {

    fun login(account: String, password: String, success: suspend () -> Unit) {
        safeLaunch {
            if (!checkFormatIsOK(account, password)) {
                return@safeLaunch
            }
            event.showDialog()
            val account2 = if (account.length == 10 && account.getOrNull(0) == '0') {
                account.drop(1)
            } else {
                account
            }
            try {
                val response = Repository.user.login(account2, password)
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
                    Repository.user.saveLoginOnly(user)
                    Repository.user.save(user)
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