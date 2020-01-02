package cn.ifafu.ifafu.mvp.web

import cn.ifafu.ifafu.base.mvp.IModel
import cn.ifafu.ifafu.base.mvp.IPresenter
import cn.ifafu.ifafu.base.mvp.IView
import io.reactivex.Observable

class WebContract {
    interface Presenter: IPresenter

    interface View : IView {
        fun loadUrl(url: String)

        fun setTitle(title: String? = null, resId: Int? = null)
    }

    interface Model: IModel {
        /**
         * 获取首页Url
         *
         * @return 首页Url
         */
        fun getMainUrl(): String

        fun loadMainHtml(): Observable<MutableMap<String, String>>
    }

}
