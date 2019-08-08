package cn.ifafu.ifafu;

import com.alibaba.fastjson.JSONObject;

import org.junit.Test;

import java.util.Scanner;

import cn.ifafu.ifafu.http.RetrofitManager;
import cn.ifafu.ifafu.http.service.WeatherService;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class ExampleUnitTest {

    Scheduler mainThread = Schedulers.single();
    Scheduler ioThread = Schedulers.single();

    @Test
    public void test() {

        Observable
                .<String>create(emitter -> {
                    l("create");
                    emitter.onNext("111");
                    emitter.onComplete();
                })
                .subscribeOn(ioThread)
                .observeOn(mainThread)
                .doOnNext(s -> {
                    l("doOnNext: " + s);
                })
                .map(s -> {
                    l("map： " + s);
                    return s + "  map";
                })
                .subscribeOn(ioThread)
                .observeOn(mainThread)
                .subscribe(s -> {
                    l("onNext: " + s);
                }, Throwable::printStackTrace);


        Scanner in = new Scanner(System.in);
        in.next();
    }

    public Observable<JSONObject> getWeather(String cityCode) {
        return Observable.create(emitter -> {
            WeatherService service = RetrofitManager.INSTANCE.obtainService(WeatherService.class, "http://www.weather.com.cn");
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
