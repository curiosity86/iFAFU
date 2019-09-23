package cn.ifafu.ifafu.mvp.main

import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.base.i.IView
import cn.ifafu.ifafu.base.ifafu.IZFModel
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.entity.Weather
import io.reactivex.Observable

class BaseMainContract {

    interface View : IView {

        fun setCheckoutDialogData(users: List<User>)

        fun showCheckoutDialog()

        fun hideCheckoutDialog()
    }

    interface Presenter: IPresenter {

        fun updateApp()

        fun checkout()

        fun checkoutTheme()

        fun addAccountSuccess();

        fun deleteUser(user: User)

        fun checkoutTo(user: User)
    }

    interface Model: IZFModel {

        fun getAllUser(): List<User>

        fun getLoginUser(): User?

        fun saveLoginUser(user: User)

        fun deleteAccount(user: User)

        fun getWeather(cityCode: String): Observable<Weather>
    }

}