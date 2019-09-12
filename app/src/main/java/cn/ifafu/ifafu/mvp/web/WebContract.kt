package cn.ifafu.ifafu.mvp.web

import cn.ifafu.ifafu.mvp.base.i.IView
import cn.ifafu.ifafu.mvp.base.i.IZFModel
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter
import io.reactivex.Observable

class WebContract {
    interface Presenter : IZFPresenter

    interface View : IView {
        fun loadUrl(url: String)

        fun setTitle(title: String? = null, resId: Int? = null)
    }

    interface Model : IZFModel {
        /**
         * 获取首页Url
         *
         * @return 首页Url
         */
        fun getMainUrl(): String

        fun getMainHtml(): Observable<String>
    }

}
