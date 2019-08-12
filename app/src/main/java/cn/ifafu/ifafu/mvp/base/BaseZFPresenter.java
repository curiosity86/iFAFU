package cn.ifafu.ifafu.mvp.base;

import android.content.Intent;
import android.util.Log;

import javax.security.auth.login.LoginException;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.exception.NoAuthException;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public abstract class BaseZFPresenter<V extends IView, M extends IZFModel> extends BasePresenter<V, M> implements IZFPresenter {

    /**
     * 通过{@link Observable#retryWhen(Function)}捕捉{@link LoginException}异常后，触发登录账号
     *
     * Need {@link LoginException}
     */
    protected ObservableSource<?> ensureTokenAlive(Observable<Throwable> throwableObservable) {
        return throwableObservable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            if (throwable instanceof NoAuthException) {
                Log.d(TAG, "需要登录账号");
                return reLogin();
            } else {
                Log.d(TAG, "抛出异常");
                return Observable.error(throwable);
            }
        });
    }

    protected Observable<Response<String>> reLogin() {
        return mModel.login(mModel.getUser())
                .compose(RxUtils.ioToMainScheduler())
                .doOnNext(response -> {
                    if (response.getCode() == Response.FAILURE) {
                        Log.d(TAG, "信息错误");
                        mView.openActivity(new Intent(mView.getContext(), LoginActivity.class));
                        mView.killSelf();
                    } else if (response.getCode() == Response.ERROR) {
                        Log.d(TAG, "登录出错");
                        throw new Exception(response.getMessage());
                    } else {
                        Log.d(TAG, "登录成功");
                    }
                });
    }

    @Override
    protected void onError(Throwable throwable) {
        super.onError(throwable);

    }
}
