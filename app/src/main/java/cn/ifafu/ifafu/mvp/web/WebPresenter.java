package cn.ifafu.ifafu.mvp.web;

import android.webkit.CookieManager;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.exception.NoAuthException;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.util.RxUtils;
import cn.ifafu.ifafu.util.SPUtils;

class WebPresenter extends BaseZFPresenter<WebContract.View, WebContract.Model> implements WebContract.Presenter {

    WebPresenter(WebContract.View view) {
        super(view, new WebModel(view.getContext()));
    }

    @Override
    public void onStart() {
        mCompDisposable.add(mModel.getMainHtml()
                .map(s -> {
                    if (s.contains("请登录")) {
                        throw new NoAuthException();
                    } else {
                        return mModel.getMainUrl();
                    }
                })
                .retryWhen(this::ensureTokenAlive)
                .compose(RxUtils.ioToMain())
                .subscribe(url -> {
                    setCookie(url);
                    mView.loadUrl(url);
                }, throwable -> {
                    mView.loadUrl(mModel.getMainUrl());
                    onError(throwable);
                })
        );
    }

    private void setCookie(String url) {
        String session = SPUtils.get(Constant.SP_COOKIE).getString("ASP.NET_SessionId");
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookies(null);
        cookieManager.flush();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, session);
    }
}
