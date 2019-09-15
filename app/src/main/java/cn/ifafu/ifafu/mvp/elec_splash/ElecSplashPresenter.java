package cn.ifafu.ifafu.mvp.elec_splash;

import android.annotation.SuppressLint;
import android.content.Intent;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.local.RepositoryImpl;
import cn.ifafu.ifafu.data.entity.ElecCookie;
import cn.ifafu.ifafu.data.entity.ElecUser;
import cn.ifafu.ifafu.mvp.elec_login.ElecLoginActivity;
import cn.ifafu.ifafu.mvp.elec_main.ElecMainActivity;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.util.AppUtils;
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
                    mView.openActivity(new Intent(mView.getContext(), b? ElecMainActivity.class : ElecLoginActivity.class));
                }, throwable -> {
                    onError(throwable);
                    mView.openActivity(new Intent(mView.getContext(), ElecLoginActivity.class));
                });
    }

    /**
     * @return true MainActivity
     *          false LoginActivity
     */
    private boolean jumpJudge() {
        ElecCookie elecCookie = RepositoryImpl.getInstance().getElecCookie();
        ElecUser elecUser = RepositoryImpl.getInstance().getElecUser();
        if (!SPUtils.get(Constant.SP_ELEC).contain("IMEI")) {
            SPUtils.get(Constant.SP_ELEC).putString("IMEI", AppUtils.imei());
        }
        if (!SPUtils.get(Constant.SP_ELEC).contain("User-Agent")) {
            SPUtils.get(Constant.SP_ELEC).putString("User-Agent", AppUtils.getUserAgent());
        }
        return elecCookie != null && elecUser != null;
    }
}
