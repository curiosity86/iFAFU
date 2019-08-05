package cn.ifafu.ifafu.mvp.login;

import android.content.Intent;
import android.util.Log;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.http.parser.LoginParser;
import cn.ifafu.ifafu.http.parser.VerifyParser;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.main.MainActivity;
import cn.woolsen.android.uitl.RxJavaUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class LoginPresenter extends BaseZFPresenter<LoginContract.View, LoginContract.Model>
        implements LoginContract.Presenter {

    private int comeFromWhere;

    private int schoolCode = Constant.FAFU;

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
        Log.d(TAG, "start");
        String account = mView.getAccountText();
        String password = mView.getPasswordText();
        if (!ensureFormat(account, password)) {
            return;
        }
        User user = new User(Constant.FAFU);
        user.setAccount(account);
        user.setPassword(password);
        user.setSchoolCode(schoolCode);
        Log.d(TAG, "end");
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
        if (account.charAt(0) == '0' || account.length() == 9) {
            account = account.substring(1);
            mView.setAccountText(account);
            schoolCode = Constant.FAFU_JS;
            mView.setBackgroundLogo(R.drawable.drawable_fafu_js);
        } else {
            schoolCode = Constant.FAFU;
            mView.setBackgroundLogo(R.drawable.drawable_fafu);
        }
    }

}
