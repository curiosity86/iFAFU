package cn.ifafu.ifafu.electricity.login;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.jaeger.library.StatusBarUtil;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;

public class ElecLoginActivity extends BaseActivity<ElecLoginContract.Presenter> implements ElecLoginContract.View {

    private EditText snoET;
    private EditText passwordET;
    private EditText verifyET;
    private ImageView verifyIV;
    private ProgressDialog progress;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_elec_login;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);

        mPresenter = new ElecLoginPresenter(this);

        snoET = findViewById(R.id.accountET);
        passwordET = findViewById(R.id.passwordET);
        verifyET = findViewById(R.id.verifyET);
        verifyIV = findViewById(R.id.verifyIV);
        Button loginBtn = findViewById(R.id.loginBtn);
        verifyIV.setOnClickListener( v -> mPresenter.verify());
        loginBtn.setOnClickListener(v -> mPresenter.login());

        progress = new ProgressDialog(this);
        progress.setText("加载中");
    }

    @Override
    public void setSnoEtText(String sno) {
        snoET.setText(sno);
    }

    @Override
    public void setPasswordText(String password) {
        passwordET.setText(password);
    }

    @Override
    public Editable getSNoEditable() {
        return snoET.getText();
    }

    @Override
    public Editable getPasswordEditable() {
        return passwordET.getText();
    }

    @Override
    public Editable getVerifyEditable() {
        return verifyET.getText();
    }

    @Override
    public void setVerifyBitmap(Bitmap bitmap) {
        verifyIV.setImageBitmap(bitmap);
    }

    @Override
    public void showLoading() {
        progress.show();
    }

    @Override
    public void hideLoading() {
        progress.cancel();
    }
}
