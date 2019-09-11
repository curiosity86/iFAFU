package cn.ifafu.ifafu.mvp.login;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;
import cn.ifafu.ifafu.mvp.base.BaseActivity;

public class LoginActivity extends BaseActivity<LoginContract.Presenter>
        implements LoginContract.View, View.OnClickListener, TextWatcher {

    private EditText accountET;

    private EditText passwordET;

    private ImageView logoIV;

    private ProgressDialog progressDialog;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparent(this);

        mPresenter = new LoginPresenter(this);

        logoIV = findViewById(R.id.bg_logo);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            logoIV.setVisibility(View.GONE);
        }

        ImageView backBtn = findViewById(R.id.btn_finish);
        backBtn.setOnClickListener(this);
        accountET = findViewById(R.id.et_account);
        passwordET = findViewById(R.id.et_password);
        accountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPresenter.checkAccount(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        findViewById(R.id.btn_login).setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setText(R.string.logging_in);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                mPresenter.onLogin();
                break;
            case R.id.btn_finish:
                finish();
                break;
        }
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.cancel();
    }

    @Override
    public String getAccountText() {
        return accountET.getText().toString();
    }

    @Override
    public String getPasswordText() {
        return passwordET.getText().toString();
    }

    @Override
    public void setBackgroundLogo(@DrawableRes int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        Glide.with(this)
                .load(resId)
                .into(logoIV);
    }

    @Override
    public void setAccountText(String text) {
        accountET.setText(text);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mPresenter.checkAccount(s.toString());
    }
}
