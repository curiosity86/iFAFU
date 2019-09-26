package cn.ifafu.ifafu.mvp.main

import android.content.Context
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.entity.Weather
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.service.WeatherService
import com.alibaba.fastjson.JSONObject
import io.reactivex.Observable
import java.util.*

abstract class BaseMainModel(context: Context) : BaseZFModel(context), BaseMainContract.Model {

    override fun getAllUser(): MutableList<User> {
        return repository.allUser
    }

    override fun saveLoginUser(user: User) {
        repository.saveLoginUser(user)
    }

    override fun getLoginUser(): User? {
        var user = repository.loginUser
        if (user == null) {
            user = repository.allUser.getOrNull(0)
            if (user != null) {
                saveLoginUser(user)
            }
            return user
        }
        return user
    }

    override fun deleteAccount(user: User) {
        repository.deleteUser(user)
    }

    override fun getWeather(cityCode: String): Observable<Weather> {
        return Observable.fromCallable {
            val weather = Weather()
            val referer = "http://www.weather.com.cn/weather1d/$cityCode.shtml"
            val service: WeatherService = APIManager.getWeatherAPI()

            // 获取城市名和当前温度
            val url1 = "http://d1.weather.com.cn/sk_2d/$cityCode.html"
            val body1 = service.getWeather(url1, referer).execute().body()
            var jsonStr1: String = Objects.requireNonNull(body1)!!.string()
            jsonStr1 = jsonStr1.replace("var dataSK = ", "")
            val jo1: JSONObject = JSONObject.parseObject(jsonStr1)
            weather.cityName = jo1.getString("cityname")
            weather.nowTemp = jo1.getInteger("temp")
            weather.weather = jo1.getString("weather")

            // 获取白天温度和晚上温度
            val url2 = "http://d1.weather.com.cn/dingzhi/$cityCode.html"
            val body2 = service.getWeather(url2, referer).execute().body()
            var jsonStr2: String = Objects.requireNonNull(body2)!!.string()
            jsonStr2 = jsonStr2.substring(jsonStr2.indexOf('=') + 1, jsonStr2.indexOf(";"))
            var jo2: JSONObject = JSONObject.parseObject(jsonStr2)
            jo2 = jo2.getJSONObject("weatherinfo")
            weather.amTemp = Integer.valueOf(jo2.getString("temp").replace("℃", ""))
            weather.pmTemp = Integer.valueOf(jo2.getString("tempn").replace("℃", ""))
            weather
        }
    }
}