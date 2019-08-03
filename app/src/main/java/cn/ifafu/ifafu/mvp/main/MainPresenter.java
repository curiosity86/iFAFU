package cn.ifafu.ifafu.mvp.main;

import android.content.Intent;

import cn.woolsen.android.mvp.BasePresenter;
import cn.ifafu.ifafu.mvp.login.LoginActivity;

class MainPresenter extends BasePresenter<MainContract.View, MainContract.Model>
        implements MainContract.Presenter {

    MainPresenter(MainContract.View view) {
        super(view, new MainModel(view.getContext()));
    }

    @Override
    public void onStart() {
        //获取主页菜单
        mCompDisposable.add(mModel.getMenus()
                .subscribe(menus -> {
                    mView.setMenuAdapterData(menus);
                }, this::onError));
        mView.setLeftMenuHeadName(mModel.getUserName());
        mView.setLeftMenuHeadIcon(mModel.getSchoolIcon());
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
