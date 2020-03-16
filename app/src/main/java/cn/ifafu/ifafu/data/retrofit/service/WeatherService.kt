package cn.ifafu.ifafu.data.retrofit.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface WeatherService {
    @GET
    fun getWeather(
            @Url url: String,
            @Header("Referer") referer: String
    ): Call<ResponseBody?>
}