package cn.ifafu.ifafu.mvp.elec_login

import android.graphics.Bitmap

import cn.ifafu.ifafu.data.entity.ElecCookie
import cn.ifafu.ifafu.data.entity.ElecQuery
import cn.ifafu.ifafu.data.entity.ElecUser
import cn.ifafu.ifafu.mvp.base.i.IModel
import cn.ifafu.ifafu.mvp.base.i.IPresenter
import cn.ifafu.ifafu.mvp.base.i.IView
import io.reactivex.Observable

class ElecLoginContract {

    interface View : IView {

        fun getSnoText(): String
        fun getPasswordText(): String
        fun getVerifyText(): String

        fun setSnoEtText(sno: String?)

        fun setPasswordText(password: String?)

        /**
         * 设置验证码
         */
        fun setVerifyBitmap(bitmap: Bitmap)
    }

    interface Model : IModel {

        fun getUser(): ElecUser?

        /**
         * 获取验证码
         */
        fun verifyBitmap(): Observable<Bitmap>

        /**
         * 登录
         */
        fun login(sno: String, password: String, verify: String): Observable<String>

        /**
         * 保存用户信息和RescouseTypeCookie
         */
        fun save(user: ElecUser)

        fun save(elecQuery: ElecQuery)

        fun save(elecCookie: ElecCookie)
    }

    interface Presenter : IPresenter {
        /**
         * 刷新验证码
         */
        fun verify()

        fun login()
    }
}
