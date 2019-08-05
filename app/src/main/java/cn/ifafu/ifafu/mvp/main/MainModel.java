package cn.ifafu.ifafu.mvp.main;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.data.Weather;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.http.RetrofitFactory;
import cn.ifafu.ifafu.http.service.WeatherService;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import cn.ifafu.ifafu.data.Menu;
import cn.ifafu.ifafu.mvp.exam.ExamActivity;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

class MainModel extends BaseZFModel implements MainContract.Model {

    private User user;

    MainModel(Context context) {
        super(context);
        user = IFAFU.getUser();
    }

    @Override
    public Observable<List<Menu>> getMenus() {
        return Observable.create(emitter -> {
            List<Menu> menus = new ArrayList<>();
            menus.add(new Menu(R.drawable.tab_syllabus, "课程表", SyllabusActivity.class));
            menus.add(new Menu(R.drawable.tab_exam, "考试查询", ExamActivity.class));
            emitter.onNext(menus);
            emitter.onComplete();
        });
    }

    @Override
    public Drawable getSchoolIcon() {
        switch (user.getSchoolCode()) {
            case Constant.FAFU:
                return mContext.getDrawable(R.drawable.drawable_fafu2);
            case Constant.FAFU_JS:
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
        return Observable.create(emitter -> {
            Weather weather = new Weather();
            String referer = "http://www.weather.com.cn/weather1d/" + cityCode + ".shtml";
            WeatherService service = RetrofitFactory.obtainServiceTemp(WeatherService.class, "http://www.weather.com.cn");

            // 获取城市名和当前温度
            String url1 = "http://d1.weather.com.cn/sk_2d/" + cityCode + ".html";
            ResponseBody body1 = service.getWeather(url1, referer).execute().body();
            if (body1 == null) {
                throw new NetworkErrorException("获取天气信息异常");
            }
            String jsonStr1 = body1.string();
            jsonStr1 = jsonStr1.replace("var dataSK = ", "");
            JSONObject jo1 = JSONObject.parseObject(jsonStr1);
            weather.setCityName(jo1.getString("cityname"));
            weather.setNowTemp(jo1.getInteger("temp"));
            weather.setWeather(jo1.getString("weather"));

            // 获取白天温度和晚上温度
            String url2 = "http://d1.weather.com.cn/dingzhi/" + cityCode + ".html";
            ResponseBody body2 = service.getWeather(url2, referer).execute().body();
            if (body2 == null) {
                throw new NetworkErrorException("获取天气信息异常");
            }
            String jsonStr2 = body2.string();
            jsonStr2 = jsonStr2.substring(0, jsonStr2.indexOf(";"))
                    .replace("var cityDZ" + cityCode + " =", "");
            JSONObject jo2 = JSONObject.parseObject(jsonStr2);
            jo2 = jo2.getJSONObject("weatherinfo");
            weather.setAmTemp(Integer.valueOf(jo2.getString("temp").replace("℃", "")));
            weather.setPmTemp(Integer.valueOf(jo2.getString("tempn").replace("℃", "")));

            emitter.onNext(weather);
            emitter.onComplete();
        });
    }
}
