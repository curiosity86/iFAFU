package cn.ifafu.ifafu.data.new_http.impl

import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.bean.Version
import cn.ifafu.ifafu.ui.main.bean.Weather
import cn.ifafu.ifafu.data.new_http.bean.WoResponse
import cn.ifafu.ifafu.data.new_http.WoService
import cn.ifafu.ifafu.util.HttpClient
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Headers

class WoServiceImpl : WoService {

    private val http = HttpClient()

    override suspend fun getOpeningDay(): IFResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = http.get("${Constant.WOOLSEN_BASE_URL}/api/text/OpeningDay")
            val wResp = JSONObject.parseObject(response.body()?.string()
                    ?: "", WoResponse::class.java)
            IFResult.success(wResp.data as String)
        } catch (e: JSONException) {
            IFResult.failure<String>("开学时间获取出错")
        } catch (e: Exception) {
            IFResult.failure<String>(e)
        }
    }

    override suspend fun getWeather(code: String): IFResult<Weather> {
        val weather = Weather()
        val referer = "http://www.weather.com.cn/weather1d/$code.shtml"

        // 获取城市名和当前温度
        val url1 = "http://d1.weather.com.cn/sk_2d/$code.html"
        val body1 = http.get(url1, Headers.of("Referer", referer)).body()
        var jsonStr1: String = body1!!.string()
        jsonStr1 = jsonStr1.replace("var dataSK = ", "")
        val jo1: JSONObject = JSONObject.parseObject(jsonStr1)
        weather.cityName = jo1.getString("cityname")
        weather.nowTemp = jo1.getInteger("temp")
        weather.weather = jo1.getString("weather")

        // 获取白天温度和晚上温度
        val url2 = "http://d1.weather.com.cn/dingzhi/$code.html"
        val body2 = http.get(url2, Headers.of("Referer", referer)).body()
        var jsonStr2: String = body2!!.string()
        jsonStr2 = jsonStr2.substring(jsonStr2.indexOf('=') + 1, jsonStr2.indexOf(";"))
        var jo2: JSONObject = JSONObject.parseObject(jsonStr2)
        jo2 = jo2.getJSONObject("weatherinfo")
        weather.amTemp = Integer.valueOf(jo2.getString("temp").replace("℃", ""))
        weather.pmTemp = Integer.valueOf(jo2.getString("tempn").replace("℃", ""))
        return IFResult.success(weather)
    }

    override suspend fun getNewVersion(): IFResult<Version>  {
        val resp =  http.get("${Constant.WOOLSEN_BASE_URL}/api/text/NewVersion")
        val wResp =  JSONObject.parseObject(resp.body()?.string()
                ?: "", WoResponse::class.java)
        val data = wResp.data as? String
        return if (data != null) {
            val split = data.split("|")
            IFResult.success(Version(split[0], split[1].toInt()))
        } else {
            IFResult.failure("无法获取到最新版本")
        }
    }

}