package cn.ifafu.ifafu.mvp.main;

import android.content.Intent;

import cn.ifafu.ifafu.BuildConfig;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.util.RxUtils;

public class MainPresenter extends BaseZFPresenter<MainContract.View, MainContract.Model>
        implements MainContract.Presenter {

    MainPresenter(MainContract.View view) {
        mView = view;
        mModel = new MainModel(view.getContext());
    }

    @Override
    public void onStart() {
        mView.setLeftMenuHeadName(mModel.getUserName());
        mView.setLeftMenuHeadIcon(mModel.getSchoolIcon());
        update();
    }

    @Override
    public void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "iFAFU下载链接：http://ifafu.cn");
        mView.openActivity(Intent.createChooser(intent, "分享"));
    }

    @Override
    public void update() {
        if (mView.getActivity().getIntent().getIntExtra("come_from", -1) != 0){
            mCompDisposable.add(
                reLogin()
                        .compose(RxUtils.ioToMainScheduler())
                        .subscribe(response -> {}, this::onError)
            );
        }
        // 获取主页菜单
        mCompDisposable.add(mModel.getMenus()
                .compose(RxUtils.ioToMainScheduler())
                .subscribe(menus -> mView.setMenuAdapterData(menus), this::onError)
        );
        // 获取天气
        mCompDisposable.add(mModel.getWeather("101230101")
                .compose(RxUtils.ioToMainScheduler())
                .subscribe(weather -> mView.setWeatherText(weather), this::onError)
        );
    }

    @Override
    public void quitAccount() {
        if (BuildConfig.DEBUG) {
            mModel.clearAllDate();
        }
        Intent intent = new Intent(mView.getContext(), LoginActivity.class);
        mView.openActivity(intent);
        mView.killSelf();
    }
}
