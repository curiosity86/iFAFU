package cn.ifafu.ifafu.mvp.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;

public abstract class BaseActivity<P extends IPresenter> extends AppCompatActivity implements IView {

    protected P mPresenter;
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void showMessage(String msg) {
//        View view = this.getWindow().getDecorView().findViewById(android.R.id.content);
//        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showMessage(int stringRes) {
        Toast.makeText(this, stringRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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
        super.onDestroy();
    }

}
