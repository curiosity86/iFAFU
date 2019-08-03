package cn.ifafu.ifafu.mvp.login;

import android.content.Intent;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.http.parser.LoginParser;
import cn.ifafu.ifafu.http.parser.VerifyParser;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.main.MainActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class LoginPresenter extends BaseZFPresenter<LoginContract.View, LoginContract.Model>
        implements LoginContract.Presenter {

    private int comeFromWhere;

    private int schoolCode = Constant.FAFU;

    private VerifyParser verifyParser;

    LoginPresenter(LoginContract.View view) {
        super(view, new LoginModel(view.getContext()));
        verifyParser = new VerifyParser(view.getContext());
    }

    @Override
    public void onStart() {
        Intent intent = mView.getActivity().getIntent();
        comeFromWhere = intent.getIntExtra("come_from", 0);
    }

    @Override
    public void onLogin() {
        String account = mView.getAccountText();
        String password = mView.getPasswordText();
        User user = new User(Constant.FAFU);
        user.setAccount(account);
        user.setPassword(password);
        user.setSchoolCode(schoolCode);
        mCompDisposable.add(mModel.login(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(result -> {
                    if (!result.isSuccess()) {
                        mView.showMessage(result.getMessage());
                    } else {
                        //登录成功
                        mView.showMessage("登录成功");
                        if (comeFromWhere == 0) {
                            user.setName(result.getBody());
                            mModel.saveUser(user);
                            Intent intent = new Intent(mView.getContext(), MainActivity.class);
                            mView.openActivity(intent);
                            mView.killSelf();
                        } else {
                            mView.killSelf();
                        }
                    }
                }, this::onError)
        );
    }

    @Override
    public void checkAccount() {
        String account = mView.getAccountText();
        if (account.isEmpty()) return;
        if (account.charAt(0) == '0') {
            account = account.substring(1);
            mView.setAccountText(account);
        }
        if (account.length() == 9) {
            schoolCode = Constant.FAFU_JS;
            mView.setBackgroundLogo(R.drawable.drawable_fafu_js);
        } else {
            schoolCode = Constant.FAFU;
            mView.setBackgroundLogo(R.drawable.drawable_fafu);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        verifyParser = null;
    }

}
