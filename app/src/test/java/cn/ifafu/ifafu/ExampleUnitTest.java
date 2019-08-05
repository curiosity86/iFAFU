package cn.ifafu.ifafu;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import cn.ifafu.ifafu.http.RetrofitFactory;
import cn.ifafu.ifafu.http.service.WeatherService;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class ExampleUnitTest {

    Scheduler mainThread = Schedulers.newThread();
    Scheduler ioThread = Schedulers.newThread();

    @Test
    public void test() {
        ChineseNumbers.englishNumberToChinese("123");




        Scanner in = new Scanner(System.in);
        in.next();
    }

    public Observable<JSONObject> getWeather(String cityCode) {
        return Observable.create(emitter -> {
            WeatherService service = RetrofitFactory.obtainServiceTemp(WeatherService.class, "http://www.weather.com.cn");
            String url = "http://d1.weather.com.cn/sk_2d/" + cityCode + ".html";
            String referer = "http://www.weather.com.cn/weather1d/" + cityCode + ".shtml";
            String jsonStr = service.getWeather(url, referer).execute().body().string();
            jsonStr = jsonStr.replace("var dataSK = ", "");
            JSONObject jo = JSONObject.parseObject(jsonStr);
            emitter.onNext(jo);
            emitter.onComplete();
        });
    }

    private void l(String msg) {
        System.out.println(Thread.currentThread() + "     " + msg);
    }

}
