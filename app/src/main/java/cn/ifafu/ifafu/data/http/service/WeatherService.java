package cn.ifafu.ifafu.data.http.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

public interface WeatherService {

    @GET
    Call<ResponseBody> getWeather(
            @Url String url,
            @Header("Referer") String referer
    );

}