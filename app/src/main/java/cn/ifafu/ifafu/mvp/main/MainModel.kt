package cn.ifafu.ifafu.mvp.main

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.data.entity.Holiday
import cn.ifafu.ifafu.data.entity.Menu
import cn.ifafu.ifafu.data.entity.Weather
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.service.WeatherService
import cn.ifafu.ifafu.electricity.splash.ElecSplashActivity
import cn.ifafu.ifafu.mvp.base.BaseZFModel
import cn.ifafu.ifafu.mvp.exam.ExamActivity
import cn.ifafu.ifafu.mvp.main.MainContract.Model
import cn.ifafu.ifafu.mvp.score.ScoreActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel
import cn.ifafu.ifafu.mvp.web.WebActivity
import com.alibaba.fastjson.JSONObject
import io.reactivex.Observable
import java.util.*

class MainModel(context: Context?) : BaseZFModel(context), Model {

    override fun getMenus(): Observable<List<Menu>> {
        return Observable.fromCallable {
            listOf(Menu(drawable(R.drawable.tab_syllabus), "课程表", intent(SyllabusActivity::class.java)),
                    Menu(drawable(R.drawable.tab_exam), "考试计划", intent(ExamActivity::class.java)),
                    Menu(drawable(R.drawable.tab_score), "成绩查询", intent(ScoreActivity::class.java)),
                    Menu(drawable(R.drawable.tab_web), "网页模式", intent(WebActivity::class.java)),
                    Menu(drawable(R.drawable.tab_electricity), "电费查询", intent(ElecSplashActivity::class.java)),
                    Menu(drawable(R.drawable.tab_repair), "报修服务", intent(WebActivity::class.java).apply {
                        putExtra("title", "报修服务")
                        putExtra("url", Constant.REPAIR_URL)
                    })
            )
        }
    }

    private fun drawable(@DrawableRes id: Int): Drawable? = mContext.getDrawable(id)
    private fun intent(cls: Class<*>): Intent = Intent(mContext, cls)

    override fun getSchoolIcon(): Drawable? {
        return when (repository.user.schoolCode) {
            School.FAFU -> drawable(R.drawable.drawable_fafu_b_white)
            School.FAFU_JS -> drawable(R.drawable.drawable_fafu_js_white)
            else -> drawable(R.mipmap.ic_launcher_round)
        }
    }

    override fun getUserName(): String {
        return repository.user.name
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

    override fun getHoliday(): List<Holiday> {
        return SyllabusModel(mContext).holidays
    }

    override fun clearAllDate() {
        repository.clearAllData()
    }
}