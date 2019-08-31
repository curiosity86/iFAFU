package cn.ifafu.ifafu.electricity.splash;

import android.annotation.SuppressLint;
import android.content.Intent;

import cn.ifafu.ifafu.electricity.login.ElecLoginActivity;
import cn.ifafu.ifafu.electricity.main.ElecMainActivity;
import cn.ifafu.ifafu.electricity.util.SPUtils;
import cn.ifafu.ifafu.electricity.util.StringUtils;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ElecSplashPresenter extends BasePresenter<ElecSplashContract.View, IModel>
        implements ElecSplashContract.Presenter {

    ElecSplashPresenter(ElecSplashContract.View view) {
        super(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onStart() {
        Observable
                .<Boolean>create(emitter -> {
                    SPUtils.init(mView.getContext());
                    emitter.onNext(jumpJudge());
                    emitter.onComplete();
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> mView.killSelf())
                .subscribe(b -> {
                    if (b) {
                        mView.openActivity(new Intent(mView.getContext(), ElecMainActivity.class));
                    } else {
                        mView.openActivity(new Intent(mView.getContext(), ElecLoginActivity.class));
                    }
                }, throwable -> {
                    onError(throwable);
                    mView.openActivity(new Intent(mView.getContext(), ElecLoginActivity.class));
                });
    }

    private boolean jumpJudge() {
        boolean b = SPUtils.get("Cookie").contain("sourcetypeticket") && SPUtils.get("UserInfo").contain("account");
        if (!b) {
            SPUtils.get("Cookie").clear();
            SPUtils.get("UserInfo").clear();
        }
//        Log.d("ElecSplashActivity", "IMEI ==> " + StringUtils.imei());
//        Log.d("ElecSplashActivity", "User-Agent ==> " + StringUtils.getUserAgent());
        if (!SPUtils.get("Const").contain("IMEI")) {
            SPUtils.get("Const").putString("IMEI", StringUtils.imei());
        }
        if (!SPUtils.get("Const").contain("User-Agent")) {
            SPUtils.get("Const").putString("User-Agent", StringUtils.getUserAgent());
        }
        return b;
    }
}
