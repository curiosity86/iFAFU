package cn.ifafu.ifafu.mvp.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.jaeger.library.StatusBarUtil;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.view.dialog.LoadingDialog;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;
import cn.woolsen.android.mvp.BaseActivity;

public class LoginActivity extends BaseActivity<LoginContract.Presenter>
        implements LoginContract.View, View.OnClickListener, View.OnFocusChangeListener {

    private EditText accountET;

    private EditText passwordET;

    private ImageView logoIV;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparent(this);

        mPresenter = new LoginPresenter(this);

        ImageView backBtn = findViewById(R.id.btn_finish);
        accountET = findViewById(R.id.et_account);
        passwordET = findViewById(R.id.et_password);
        logoIV = findViewById(R.id.bg_logo);

        accountET.setOnFocusChangeListener(this);
        backBtn.setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setText(R.string.logging_in);

        mPresenter.onStart();
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
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.et_account && !hasFocus) {
            mPresenter.checkAccount();
        }
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
        logoIV.setImageResource(resId);
    }

    @Override
    public void setAccountText(String text) {
        accountET.setText(text);
    }
}
