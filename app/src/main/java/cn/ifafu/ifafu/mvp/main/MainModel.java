package cn.ifafu.ifafu.mvp.main;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.dao.DaoSession;
import cn.ifafu.ifafu.data.entity.Holiday;
import cn.ifafu.ifafu.data.entity.Menu;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.Weather;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.service.WeatherService;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.electricity.splash.ElecSplashActivity;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import cn.ifafu.ifafu.mvp.exam.ExamActivity;
import cn.ifafu.ifafu.mvp.score.ScoreActivity;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity;
import cn.ifafu.ifafu.mvp.web.WebActivity;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class MainModel extends BaseZFModel implements MainContract.Model {

    private User user = repository.getUser();

    MainModel(Context context) {
        super(context);
    }

    @Override
    public Observable<List<Menu>> getMenus() {
        return Observable.fromCallable(() -> {
            List<Menu> menus = new ArrayList<>();
            menus.add(new Menu(mContext.getDrawable(R.drawable.tab_syllabus), "课程表", SyllabusActivity.class));
            menus.add(new Menu(mContext.getDrawable(R.drawable.tab_exam), "考试计划", ExamActivity.class));
            menus.add(new Menu(mContext.getDrawable(R.drawable.tab_score), "成绩查询", ScoreActivity.class));
            menus.add(new Menu(mContext.getDrawable(R.drawable.tab_web), "网页模式", WebActivity.class));
            menus.add(new Menu(mContext.getDrawable(R.drawable.ic_electricity), "电费查询", ElecSplashActivity.class));
            return menus;
        });
    }

    @Override
    public Drawable getSchoolIcon() {
        switch (user.getSchoolCode()) {
            case School.FAFU:
                return mContext.getDrawable(R.drawable.drawable_fafu2);
            case School.FAFU_JS:
                return mContext.getDrawable(R.drawable.drawable_fafu_js2);
            default:
                return mContext.getDrawable(R.drawable.drawable_ifafu2);
        }
    }

    @Override
    public String getUserName() {
        return user.getName();
    }

    @Override
    public Observable<Weather> getWeather(String cityCode) {
        return Observable.fromCallable(() -> {
            Weather weather = new Weather();
            String referer = "http://www.weather.com.cn/weather1d/" + cityCode + ".shtml";
            WeatherService service = APIManager.getWeatherAPI();

            // 获取城市名和当前温度
            String url1 = "http://d1.weather.com.cn/sk_2d/" + cityCode + ".html";
            ResponseBody body1 = service.getWeather(url1, referer).execute().body();
            String jsonStr1 = Objects.requireNonNull(body1).string();
            jsonStr1 = jsonStr1.replace("var dataSK = ", "");
            JSONObject jo1 = JSONObject.parseObject(jsonStr1);
            weather.setCityName(jo1.getString("cityname"));
            weather.setNowTemp(jo1.getInteger("temp"));
            weather.setWeather(jo1.getString("weather"));

            // 获取白天温度和晚上温度
            String url2 = "http://d1.weather.com.cn/dingzhi/" + cityCode + ".html";
            ResponseBody body2 = service.getWeather(url2, referer).execute().body();
            String jsonStr2 = Objects.requireNonNull(body2).string();
            jsonStr2 = jsonStr2.substring(jsonStr2.indexOf('=')+1, jsonStr2.indexOf(";"));
            JSONObject jo2 = JSONObject.parseObject(jsonStr2);
            jo2 = jo2.getJSONObject("weatherinfo");
            weather.setAmTemp(Integer.valueOf(jo2.getString("temp").replace("℃", "")));
            weather.setPmTemp(Integer.valueOf(jo2.getString("tempn").replace("℃", "")));

            return weather;
        });
    }

    @Override
    public List<Holiday> getHoliday() {
        List<Holiday> holidays = new ArrayList<>();
        holidays.add(new Holiday("教师节", "2019-09-10"));
        holidays.add(new Holiday("中秋节", "2019-09-13"));
        holidays.add(new Holiday("国庆节", "2019-10-01"));
        holidays.add(new Holiday("圣诞节", "2019-12-25"));
        holidays.add(new Holiday("元旦", "2020-01-01"));
        holidays.add(new Holiday("春节", "2020-01-25"));
        holidays.add(new Holiday("清明节", "2020-04-04"));
        holidays.add(new Holiday("劳动节", "2020-05-01"));
        return holidays;
    }

    @Override
    public void clearAllDate() {
        SPUtils.get(Constant.SP_SETTING).clear();
        SPUtils.get(Constant.SP_USER_INFO).clear();
        DaoSession daoSession = DaoManager.getInstance().getDaoSession();
        daoSession.getScoreDao().deleteAll();
        daoSession.getCourseDao().deleteAll();
        daoSession.getExamDao().deleteAll();
        daoSession.getUserDao().deleteAll();
    }
}