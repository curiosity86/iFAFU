package cn.ifafu.ifafu.mvp.main;

import android.graphics.drawable.Drawable;

import java.util.List;

import cn.ifafu.ifafu.data.Menu;
import cn.woolsen.android.mvp.i.IModel;
import cn.woolsen.android.mvp.i.IPresenter;
import cn.woolsen.android.mvp.i.IView;
import io.reactivex.Observable;

class MainContract {

    interface View extends IView {

        void setMenuAdapterData(List<Menu> menus);

        void setLeftMenuHeadIcon(Drawable headIcon);

        void setLeftMenuHeadName(String name);
    }

    interface Model extends IModel {

        /**
         * 获取主页菜单信息
         */
        Observable<List<Menu>> getMenus();

        Drawable getSchoolIcon();

        String getUserName();
    }

    interface Presenter extends IPresenter {

        /**
         * 分享应用
         */
        void shareApp();

        /**
         * 退出账号
         */
        void quitAccount();
    }

}
