package cn.ifafu.ifafu.electricity.splash;

import android.Manifest;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.jaeger.library.StatusBarUtil;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ElecSplashActivity extends BaseActivity<ElecSplashContract.Presenter>
        implements ElecSplashContract.View {

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_elec_splash;
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        mPresenter = new ElecSplashPresenter(this);
    }
}
