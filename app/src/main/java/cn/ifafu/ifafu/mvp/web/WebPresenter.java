package cn.ifafu.ifafu.mvp.web;

import android.content.Intent;

import javax.security.auth.login.LoginException;

import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.base.IZFModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

class WebPresenter extends BaseZFPresenter<WebContract.View, WebContract.Model> implements WebContract.Presenter {

    WebPresenter(WebContract.View view) {
        super(view, new WebModel(view.getContext()));
    }

    @Override
    public void onStart() {
        Intent intent = mView.getActivity().getIntent();
        if (intent.hasExtra("title") && intent.hasExtra("url")) {
            String title = intent.getStringExtra("title");
            String url = intent.getStringExtra("url");
            mView.setTitle(title);
            mView.loadUrl(url);
        } else {
//            mCompDisposable.add(Observable
//                    .<String>create(emitter -> {
//                        if (mModel.isTokenAlive(mModel.getUser())) {
//                            emitter.onNext(mModel.getMainUrl());
//                        } else {
//                            throw new LoginException();
//                        }
//                        emitter.onComplete();
//                    })
//                    .retryWhen(this::ensureTokenAlive)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnSubscribe(d -> mView.showLoading())
//                    .subscribe(url -> mView.loadUrl(url), throwable -> {
//                        mView.loadUrl(mModel.getMainUrl());
//                        throwable.printStackTrace();
//                    })
//            );
            mCompDisposable.add(mModel.isTokenAlive(mModel.getUser())
                    .map(aBoolean -> {
                        if (aBoolean) {
                            return mModel.getMainUrl();
                        } else {
                            throw new LoginException();
                        }
                    })
                    .retryWhen(this::ensureTokenAlive)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(d -> mView.showLoading())
                    .subscribe(url -> mView.loadUrl(url), throwable -> {
                        mView.loadUrl(mModel.getMainUrl());
                        throwable.printStackTrace();
                    })
            );
        }
    }

}
