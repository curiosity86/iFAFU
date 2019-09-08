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
    public void onCreate() {
        String title = mView.getActivity().getIntent().getStringExtra("title");
        String url = mView.getActivity().getIntent().getStringExtra("url");
        if (title != null && url != null) {
            mView.setTitle(title);
            mView.loadUrl(url);
        } else {
            loadZF();
        }
    }

    private void loadZF() {
        mCompDisposable.add(mModel.getMainHtml()
                .map(s -> {
                    if (s.contains("请登录")) {
                        throw new NoAuthException();
                    } else {
                        String url = mModel.getMainUrl();
                        setCookie(url, SPUtils.get(Constant.SP_COOKIE).getString("ASP.NET_SessionId"));
                        return url;
                    }
                })
                .compose(RxUtils.ioToMain())
                .subscribe(url -> {
                    mView.loadUrl(url);
                }, throwable -> {
                    mView.loadUrl(mModel.getMainUrl());
                    onError(throwable);
                })
        );
    }

    private void setCookie(String url, String cookie) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.flush();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookie);
    }
}
