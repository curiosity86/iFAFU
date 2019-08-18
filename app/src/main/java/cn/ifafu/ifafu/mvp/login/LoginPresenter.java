package cn.ifafu.ifafu.mvp.login;

import android.content.Intent;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.main.MainActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class LoginPresenter extends BaseZFPresenter<LoginContract.View, LoginContract.Model>
        implements LoginContract.Presenter {

    private int comeFromWhere;

    private int schoolCode = School.FAFU;

    LoginPresenter(LoginContract.View view) {
        super(view, new LoginModel(view.getContext()));
    }

    @Override
    public void onStart() {
        Intent intent = mView.getActivity().getIntent();
        comeFromWhere = intent.getIntExtra("come_from", 0);
        mView.setBackgroundLogo(R.drawable.drawable_fafu);
    }

    @Override
    public void onLogin() {
        String account = mView.getAccountText();
        String password = mView.getPasswordText();
        if (!ensureFormat(account, password)) {
            return;
        }
        User user = new User();
        if (account.charAt(0) == '0') {
            account = account.substring(1);
        }
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
                        mView.showMessage(R.string.login_successful);
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

    /**
     * 确保账号密码格式正确
     */
    private boolean ensureFormat(String account, String password) {
        if (account.isEmpty()) {
            mView.showMessage(R.string.empty_account);
            return false;
        }
        if (password.isEmpty()) {
            mView.showMessage(R.string.empty_password);
            return false;
        }
        return true;
    }

    @Override
    public void checkAccount(String account) {
        if (account.isEmpty() || account.length() < 9) return;
        if (schoolCode != School.FAFU_JS && account.charAt(0) == '0' || account.length() == 9) {
            schoolCode = School.FAFU_JS;
            mView.setBackgroundLogo(R.drawable.drawable_fafu_js);
        } else if (schoolCode != School.FAFU) {
            schoolCode = School.FAFU;
            mView.setBackgroundLogo(R.drawable.drawable_fafu);
        }
    }

}
