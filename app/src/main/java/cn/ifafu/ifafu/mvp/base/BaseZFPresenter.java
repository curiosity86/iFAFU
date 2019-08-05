package cn.ifafu.ifafu.mvp.base;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.data.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.woolsen.android.mvp.BasePresenter;
import cn.woolsen.android.mvp.i.IView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class BaseZFPresenter<V extends IView, M extends IZFModel> extends BasePresenter<V, M> implements IZFPresenter {

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
        return throwableObservable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            if (!(throwable instanceof LoginException)) {
                Log.d("Syllabus", "抛出异常");
                return Observable.error(throwable);
            } else {
                Log.d("Syllabus", "登录账号");
                return mModel.login(mModel.getUser())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(stringResponse -> {
                            if (stringResponse.getCode() == Response.FAILURE) {
                                Log.d("Syllabus", "信息错误");
                                mView.openActivity(new Intent(mView.getContext(), LoginActivity.class));
                                mView.killSelf();
                            } else if (stringResponse.getCode() == Response.ERROR) {
                                Log.d("Syllabus", "登录出错");
                                throw new Exception(stringResponse.getMessage());
                            } else {
                                Log.d("Syllabus", "登录成功");
                            }
                        });
            }
        });
    }

    @Override
    protected void onError(Throwable throwable) {
        super.onError(throwable);

    }
}
