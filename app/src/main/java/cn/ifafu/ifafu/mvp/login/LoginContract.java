package cn.ifafu.ifafu.mvp.login;

import androidx.annotation.DrawableRes;

import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import io.reactivex.Observable;

class LoginContract {

    interface View extends IView {

        String getAccountText();

        String getPasswordText();

        void setAccountText(String text);

        void setBackgroundLogo(@DrawableRes int resId);
    }

    interface Presenter extends IZFPresenter {

        void onLogin();

        void checkAccount(String account);

    }

    interface Model extends IZFModel {

        Observable<Response<String>> login(User user);

        void saveUser(User user);
    }

}
