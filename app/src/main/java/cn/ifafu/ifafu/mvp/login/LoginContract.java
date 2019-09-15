package cn.ifafu.ifafu.mvp.login;

import androidx.annotation.DrawableRes;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter;
import io.reactivex.Observable;

public class LoginContract {

    public interface View extends IView {

        String getAccountText();

        String getPasswordText();

        void showCloseBtn();

        void setBackgroundLogo(@DrawableRes int resId);
    }

    public interface Presenter extends IZFPresenter {

        void onLogin();

        void checkAccount(String account);

    }

    public interface Model extends IZFModel {

        Observable<Response<String>> login(User user);

        void saveUser(User user);
    }

}
