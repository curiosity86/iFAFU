package cn.ifafu.ifafu.mvp.main;

import android.graphics.drawable.Drawable;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Holiday;
import cn.ifafu.ifafu.data.entity.Menu;
import cn.ifafu.ifafu.data.entity.Weather;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.view.timeline.TimeAxis;
import io.reactivex.Observable;

public class MainContract {

    interface View extends IView {

        void setMenuAdapterData(List<Menu> menus);

        void setLeftMenuHeadIcon(Drawable headIcon);

        void setLeftMenuHeadName(String name);

        void setWeatherText(Weather weather);

        void setCourseText(String title, String name, String address, String time);

        void setTimeLineData(List<TimeAxis> data);
    }

    public interface Presenter extends IPresenter {

        void updateApp();

        void shareApp();

        void onRefresh();

        void updateWeather();

        void updateNextCourseView();

        void updateTimeLine();

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

        List<Holiday> getHoliday();

        /**
         * used in DEBUG
         */
        void clearAllDate();
    }

}
