package cn.ifafu.ifafu.mvp.login

import androidx.annotation.DrawableRes
import cn.ifafu.ifafu.base.i.IView
import cn.ifafu.ifafu.base.ifafu.IZFModel
import cn.ifafu.ifafu.base.ifafu.IZFPresenter
import cn.ifafu.ifafu.data.entity.Response
import cn.ifafu.ifafu.data.entity.User
import io.reactivex.Observable

class LoginContract {

    interface View : IView {

        fun getAccountText(): String

        fun getPasswordText(): String

        fun showCloseBtn()

        fun setBackgroundLogo(@DrawableRes resId: Int)
    }

    interface Presenter : IZFPresenter {

        fun onLogin()

        fun checkAccount(account: String)

    }

    interface Model : IZFModel {

        fun login(user: User): Observable<Response<String>>

        fun saveUser(user: User)
    }

}
