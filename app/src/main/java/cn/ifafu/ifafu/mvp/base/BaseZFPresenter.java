package cn.ifafu.ifafu.mvp.base;

import android.content.Intent;

import javax.security.auth.login.LoginException;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.exception.NoAuthException;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public abstract class BaseZFPresenter<V extends IView, M extends IZFModel> extends BasePresenter<V, M> implements IZFPresenter {

    protected static Disposable loginD;

    public BaseZFPresenter(V view) {
        super(view);
    }

    public BaseZFPresenter(V view, M model) {
        super(view, model);
    }

    /**
     * 通过{@link Observable#retryWhen(Function)}捕捉{@link LoginException}异常后，触发登录账号
     *
     * Need {@link LoginException}
     */
    protected ObservableSource<?> ensureTokenAlive(Observable<Throwable> throwableObservable) {
        return throwableObservable.flatMap(throwable -> {
            if (throwable instanceof NoAuthException || throwable.getMessage().contains("302")) {
                if (loginD != null) {
                    while (!loginD.isDisposed()) {
                        Thread.sleep(100);
                    }
                    return Observable.just(true);
                }
                return reLogin();
            } else {
                return Observable.error(throwable);
            }
        });
    }

    protected Observable<Response<String>> reLogin() {
        return mModel.reLogin()
                .compose(RxUtils.ioToMain())
                .doOnNext(response -> {
                    if (response.getCode() == Response.FAILURE) {
                        mView.openActivity(new Intent(mView.getContext(), LoginActivity.class));
                        mView.killSelf();
                    } else if (response.getCode() == Response.ERROR) {
                        throw new Exception(response.getMessage());
                    }
                });
    }

}
