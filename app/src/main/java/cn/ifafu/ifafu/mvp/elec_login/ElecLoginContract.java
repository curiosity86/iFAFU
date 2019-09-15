package cn.ifafu.ifafu.mvp.elec_login;

import android.graphics.Bitmap;

import cn.ifafu.ifafu.data.entity.ElecUser;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import io.reactivex.Observable;

public class ElecLoginContract {

    interface View extends IView {

        String getSNoEditable();
        String getPasswordEditable();
        String getVerifyEditable();

        void setSnoEtText(String sno);
        void setPasswordText(String password);

        /**
         * 设置验证码
         */
        void setVerifyBitmap(Bitmap bitmap);
    }

    interface Model extends IModel {

        ElecUser getUser();

        /**
         * 获取验证码
         */
        Observable<Bitmap> verifyBitmap();

        /**
         * 登录
         */
        Observable<String> login(String sno, String password, String verify);

        /**
         * 保存用户信息和RescouseTypeCookie
         */
        void save(ElecUser user, String rescouseType);
    }

    public interface Presenter extends IPresenter {
        /**
         * 刷新验证码
         */
        void verify();

        void login();
    }
}
