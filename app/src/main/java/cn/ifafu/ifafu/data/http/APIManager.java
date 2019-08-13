package cn.ifafu.ifafu.data.http;

import cn.ifafu.ifafu.data.http.service.WeatherService;
import cn.ifafu.ifafu.data.http.service.ZhengFangService;

public class APIManager {

    private static WeatherService weatherAPI;
    private static ZhengFangService zhengFangAPI;

    public static WeatherService getWeatherAPI() {
        if (weatherAPI == null) {
            weatherAPI = RetrofitManager.obtainService(WeatherService.class);
        }
        return weatherAPI;
    }

    public static ZhengFangService getZhengFangAPI() {
        if (zhengFangAPI == null) {
            zhengFangAPI = RetrofitManager.obtainService(ZhengFangService.class);
        }
        return zhengFangAPI;
    }

}