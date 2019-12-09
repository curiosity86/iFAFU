package cn.ifafu.ifafu.mvp.login

import android.app.Activity
import android.content.Intent
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFPresenter
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.mvp.main.MainActivity
import cn.ifafu.ifafu.util.RxUtils

internal class LoginPresenter(view: LoginContract.View) : BaseZFPresenter<LoginContract.View, LoginContract.Model>(view, LoginModel(view.context)), LoginContract.Presenter {

    private var comeFromWhere: Int = 0

    private var schoolCode = School.FAFU

    override fun onCreate() {
        val intent = mView.activity.intent
        comeFromWhere = intent.getIntExtra("from", 0)
        if (comeFromWhere == Constant.ACTIVITY_MAIN) {
            mView.showCloseBtn()
        }
        mView.setBackgroundLogo(R.drawable.fafu_bb_icon)
    }

    override fun onLogin() {
        var account = mView.getAccountText()
        val password = mView.getPasswordText()
        if (!formatIsOK(account, password)) {
            return
        }
        val user = User()
        if (account.length == 10 && account[0] == '0') {
            account = account.substring(1)
        }
        user.account = account
        user.password = password
        user.schoolCode = schoolCode
        mCompDisposable.add(mModel.login(user)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe { mView.showLoading() }
                .doFinally { mView.hideLoading() }
                .subscribe({ result ->
                    if (result.isSuccess) {
                        //登录成功
                        println("come from $comeFromWhere")
                        when (comeFromWhere) {
                            Constant.ACTIVITY_MAIN -> {
                                mView.activity.setResult(Activity.RESULT_OK)
                            }
                            else -> {
                                mView.showMessage(R.string.login_successful)
                                val intent = Intent(mView.context, MainActivity::class.java)
                                mView.openActivity(intent)
                            }
                        }
                        mView.killSelf()
                    } else {
                        mView.showMessage(result.message)
                    }
                }, this::onError)
        )
    }

    /**
     * 确保账号密码格式正确
     */
    private fun formatIsOK(account: String, password: String): Boolean {
        if (account.isEmpty()) {
            mView.showMessage(R.string.empty_account)
            return false
        }
        if (password.isEmpty()) {
            mView.showMessage(R.string.empty_password)
            return false
        }
        return true
    }

    override fun checkAccount(account: String) {
        if (account.isEmpty() || account.length < 9) return
        if (schoolCode != School.FAFU_JS && account[0] == '0' || account.length == 9) {
            schoolCode = School.FAFU_JS
            mView.setBackgroundLogo(R.drawable.fafu_js_icon)
        } else if (schoolCode != School.FAFU) {
            schoolCode = School.FAFU
            mView.setBackgroundLogo(R.drawable.fafu_bb_icon)
        }
    }

}
