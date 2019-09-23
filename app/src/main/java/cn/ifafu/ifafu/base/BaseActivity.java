package cn.ifafu.ifafu.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ifafu.ifafu.base.i.IPresenter;
import cn.ifafu.ifafu.base.i.IView;

public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements IView {

    protected P mPresenter;
    protected final String TAG = this.getClass().getSimpleName();
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId(savedInstanceState) != 0) {
            setContentView(getLayoutId(savedInstanceState));
        }
        unbinder = ButterKnife.bind(this);
        initData(savedInstanceState);
        if (mPresenter != null) {
            mPresenter.onCreate();
        }
    }

    public abstract int getLayoutId(@Nullable Bundle savedInstanceState);

    public abstract void initData(@Nullable Bundle savedInstanceState);

    @Override
    public void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int stringRes) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void openActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void killSelf() {
        finish();
    }

    @Override
    protected void onDestroy() {
        hideLoading();
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

}
