package cn.ifafu.ifafu.mvp.main;

import android.graphics.drawable.Drawable;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Menu;
import cn.ifafu.ifafu.data.entity.Weather;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import io.reactivex.Observable;

public class MainContract {

    interface View extends IView {

        void setMenuAdapterData(List<Menu> menus);

        void setLeftMenuHeadIcon(Drawable headIcon);

        void setLeftMenuHeadName(String name);

        void setWeatherText(Weather weather);
    }

    public interface Presenter extends IPresenter {

        /**
         * 分享应用
         */
        void shareApp();

        void update();

        /**
         * 退出账号
         */
        void quitAccount();
    }

    interface Model extends IZFModel {

        /**
         * 获取主页菜单信息
         */
        Observable<List<Menu>> getMenus();

        Drawable getSchoolIcon();

        String getUserName();

        Observable<Weather> getWeather(String cityCode);

        /**
         * used in DEBUG
         */
        void clearAllDate();
    }

}
