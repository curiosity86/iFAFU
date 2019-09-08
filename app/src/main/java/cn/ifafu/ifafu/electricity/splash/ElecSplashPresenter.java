package cn.ifafu.ifafu.electricity.splash;

import android.annotation.SuppressLint;
import android.content.Intent;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.electricity.login.ElecLoginActivity;
import cn.ifafu.ifafu.electricity.main.ElecMainActivity;
import cn.ifafu.ifafu.electricity.util.StringUtils;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.util.SPUtils;
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
    public void onCreate() {
        Observable
                .fromCallable(this::jumpJudge)
                .subscribeOn(Schedulers.io())
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
        boolean b = SPUtils.get(Constant.SP_ELEC).contain("sourcetypeticket") && SPUtils.get(Constant.SP_ELEC).contain("account");
        if (!b) {
            SPUtils.get(Constant.SP_ELEC).clear();
        }
        if (!SPUtils.get(Constant.SP_ELEC).contain("IMEI")) {
            SPUtils.get(Constant.SP_ELEC).putString("IMEI", StringUtils.imei());
        }
        if (!SPUtils.get(Constant.SP_ELEC).contain("User-Agent")) {
            SPUtils.get(Constant.SP_ELEC).putString("User-Agent", StringUtils.getUserAgent());
        }
        return b;
    }
}
