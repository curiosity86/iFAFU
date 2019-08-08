package cn.ifafu.ifafu.mvp.main

import android.accounts.NetworkErrorException
import android.content.Context
import android.graphics.drawable.Drawable

import com.alibaba.fastjson.JSONObject

import java.util.ArrayList

import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.Weather
import cn.ifafu.ifafu.data.dao.DaoManager
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.http.RetrofitManager
import cn.ifafu.ifafu.http.service.WeatherService
import cn.ifafu.ifafu.mvp.base.BaseZFModel
import cn.ifafu.ifafu.data.Menu
import cn.ifafu.ifafu.mvp.exam.ExamActivity
import cn.ifafu.ifafu.mvp.web.WebActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import cn.ifafu.ifafu.util.SPUtils
import io.reactivex.Observable
import okhttp3.ResponseBody

class MainModel(context: Context) : BaseZFModel(context), MainContract.Model {

    override fun getMenus(): Observable<List<Menu>> {
        return Observable.just(listOf(
                Menu(R.drawable.tab_syllabus, "课程表", SyllabusActivity::class.java),
                Menu(R.drawable.tab_exam, "考试计划", ExamActivity::class.java),
                Menu(R.drawable.tab_web, "网页模式", WebActivity::class.java)
        ))
    }

    /**
     * @return 学校校徽图标
     */
    override fun getSchoolIcon(): Drawable? =
            when (user.schoolCode) {
                Constant.FAFU -> mContext.getDrawable(R.drawable.drawable_fafu2)
                Constant.FAFU_JS -> mContext.getDrawable(R.drawable.drawable_fafu_js2)
                else -> mContext.getDrawable(R.drawable.drawable_ifafu2)
            }

    /**
     * @return 学生名字
     */
    override fun getUserName(): String = user.name

    /**
     * @return 天气
     */
    override fun getWeather(cityCode: String): Observable<Weather> {
        return Observable.create { emitter ->
            val weather = Weather()
            val referer = "http://www.weather.com.cn/weather1d/$cityCode.shtml"
            val service = RetrofitManager.obtainServiceTemp(WeatherService::class.java, "")

            // 获取城市名和当前温度
            val url1 = "http://d1.weather.com.cn/sk_2d/$cityCode.html"
            val body1 = service.getWeather(url1, referer).execute().body()
                    ?: throw NetworkErrorException("获取天气信息异常")
            var jsonStr1 = body1.string()
            jsonStr1 = jsonStr1.replace("var dataSK = ", "")
            val jo1 = JSONObject.parseObject(jsonStr1)
            weather.cityName = jo1.getString("cityname")
            weather.nowTemp = jo1.getInteger("temp")
            weather.weather = jo1.getString("weather")

            // 获取白天温度和晚上温度
            val url2 = "http://d1.weather.com.cn/dingzhi/$cityCode.html"
            val body2 = service.getWeather(url2, referer).execute().body()
                    ?: throw NetworkErrorException("获取天气信息异常")
            var jsonStr2 = body2.string()
            jsonStr2 = jsonStr2.substring(0, jsonStr2.indexOf(";"))
                    .replace("var cityDZ$cityCode =", "")
            var jo2 = JSONObject.parseObject(jsonStr2)
            jo2 = jo2.getJSONObject("weatherinfo")
            weather.amTemp = Integer.valueOf(jo2.getString("temp").replace("℃", ""))
            weather.pmTemp = Integer.valueOf(jo2.getString("tempn").replace("℃", ""))

            emitter.onNext(weather)
            emitter.onComplete()
        }
    }

    /**
     * DEBUG
     */
    override fun clearAllDate() {
        SPUtils.get(Constant.SP_SETTING).clear()
        SPUtils.get(Constant.SP_USER_INFO).clear()
        DaoManager.getInstance().daoSession.courseDao.deleteAll()
        DaoManager.getInstance().daoSession.examDao.deleteAll()
        DaoManager.getInstance().daoSession.noticeDao.deleteAll()
        DaoManager.getInstance().daoSession.userDao.deleteAll()
        DaoManager.getInstance().daoSession.zfUrlDao.deleteAll()
    }
}
