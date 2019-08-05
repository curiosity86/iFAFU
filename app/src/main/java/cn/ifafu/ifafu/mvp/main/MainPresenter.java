package cn.ifafu.ifafu.mvp.main;

import android.content.Intent;
import android.util.Log;

import cn.woolsen.android.mvp.BasePresenter;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class MainPresenter extends BasePresenter<MainContract.View, MainContract.Model>
        implements MainContract.Presenter {

    MainPresenter(MainContract.View view) {
        super(view, new MainModel(view.getContext()));
    }

    @Override
    public void onStart() {
        //获取主页菜单
        mCompDisposable.add(mModel.getMenus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(menus -> {
                    mView.setMenuAdapterData(menus);
                }, this::onError));
        mView.setLeftMenuHeadName(mModel.getUserName());
        mView.setLeftMenuHeadIcon(mModel.getSchoolIcon());
        mCompDisposable.add(mModel.getWeather("101230101")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weather -> {
                    Log.d(TAG, weather.toString());
                }, this::onError));
        update();
    }

    @Override
    public void update() {
        mCompDisposable.add(mModel.getWeather("101230101")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weather -> {
                    mView.setWeatherText(weather);
                }, this::onError));
    }

    @Override
    public void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "iFAFU下载链接：http://ifafu.cn");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mView.openActivity(Intent.createChooser(intent, "分享"));
    }

    @Override
    public void quitAccount() {
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        mView.openActivity(intent);
        mView.killSelf();
    }
}
