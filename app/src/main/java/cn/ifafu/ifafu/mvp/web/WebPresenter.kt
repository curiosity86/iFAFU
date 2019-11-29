package cn.ifafu.ifafu.mvp.web

import android.webkit.CookieManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.ifafu.BaseZFPresenter
import cn.ifafu.ifafu.mvp.web.WebContract.Presenter
import cn.ifafu.ifafu.mvp.web.WebContract.View
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.util.SPUtils

internal class WebPresenter(view: View) : BaseZFPresenter<View, WebContract.Model>(view, WebModel(view.context)), Presenter {

    override fun onCreate() {

        val title: String? = mView.activity.intent.getStringExtra("title")
        val referer: String? = mView.activity.intent.getStringExtra("referer")
        val url: String? = mView.activity.intent.getStringExtra("url")

        if (url != null && title != null) {
            mView.setTitle(title)
            if (referer != null) {
                setCookie(url, referer)
            }
            mView.loadUrl(url)
        } else {
            mView.setTitle(resId = R.string.title_web_mode)
            loadUrl(mModel.getMainUrl(), SPUtils.get(Constant.SP_COOKIE).getString("ASP.NET_SessionId"))
        }
    }

    private fun loadUrl(url: String, cookie: String) {
        mCompDisposable.add(mModel.loadMainHtml()
                .map {
                    setCookie(url, cookie)
                    url
                }
                .compose(RxUtils.ioToMain())
                .doOnSubscribe { mView.showLoading() }
                .doFinally { mView.hideLoading() }
                .subscribe({ mView.loadUrl(url) }, { throwable: Throwable ->
                    mView.loadUrl(mModel.getMainUrl())
                    onError(throwable)
                })
        )
    }

    private fun setCookie(url: String, cookie: String?) {
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.flush()
        cookieManager.setAcceptCookie(true)
        cookieManager.setCookie(url, cookie)
    }
}