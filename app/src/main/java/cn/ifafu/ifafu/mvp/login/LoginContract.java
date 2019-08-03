package cn.ifafu.ifafu.mvp.login;

import androidx.annotation.DrawableRes;

import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.Response;
import cn.ifafu.ifafu.mvp.base.IZFModel;
import cn.ifafu.ifafu.mvp.base.IZFPresenter;
import cn.woolsen.android.mvp.i.IView;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;

class LoginContract {

    interface View extends IView {

        String getAccountText();

        String getPasswordText();

        void setAccountText(String text);

        void setBackgroundLogo(@DrawableRes int resId);
    }

    interface Presenter extends IZFPresenter {

        void onLogin();

        void checkAccount();

    }

    interface Model extends IZFModel {

        Observable<Response<String>> login(User user);

        void saveUser(User user);
    }

}
