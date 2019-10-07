package cn.ifafu.ifafu.mvp.main

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.service.WeatherService
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel
import cn.ifafu.ifafu.util.DateUtils
import com.alibaba.fastjson.JSONObject
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseMainModel(context: Context) : BaseZFModel(context), BaseMainContract.Model {

    override fun getAllUser(): MutableList<User> {
        return repository.allUser
    }

    override fun getCourses(): Observable<List<Course>> {
        val syllabusModel = SyllabusModel(mContext)
        return Observable
                .fromCallable { syllabusModel.allCoursesFromDB }
                .flatMap { o: List<Course> ->
                    if (o.isEmpty()) {
                        syllabusModel.coursesFromNet
                    } else {
                        Observable.just(o)
                    }
                }
    }

    override fun getThisTermExams(): List<Exam> {
        val yearTerm = repository.yearTerm
        return repository.getExams(yearTerm.first, yearTerm.second)
    }

    override fun getSetting(): Setting {
        return repository.setting
    }

    override fun saveLoginUser(user: User) {
        repository.saveLoginUser(user)
    }

    override fun getNextCourse(): Observable<NextCourse> {
        val syllabusModel = SyllabusModel(mContext)
        return getCourses()
                .map { courses ->
                    val result = NextCourse()
                    val setting = syllabusModel.syllabusSetting
                    var currentWeek = syllabusModel.currentWeek
                    var currentWeekday: Int = DateUtils.getCurrentWeekday()

                    val date = SimpleDateFormat("MM月dd日", Locale.CHINA).format(Date())

                    //计算节假日
                    val holidayFromToMap = syllabusModel.holidayFromToMap
                    if (holidayFromToMap[currentWeek]?.containsKey(currentWeekday) == true) {
                        currentWeek = -1
                    } else {
                        for1@for ((week, pair) in holidayFromToMap) {
                            for ((weekday, pair2) in pair) {
                                if (pair2 != null && pair2.first == currentWeek && pair2.second == currentWeekday) {
                                    currentWeek = week
                                    currentWeekday = weekday
                                    break@for1
                                }
                            }
                        }
                    }

                    if (currentWeek <= 0 || currentWeek > setting.weekCnt) {
                        result.title = "放假了呀！！"
                        result.result = NextCourse.IN_HOLIDAY
                        result.dateText = "放假中 $date ${DateUtils.getWeekdayCN(currentWeekday)}"
                        return@map result
                    } else {
                        result.dateText = "第${currentWeek}周 $date ${DateUtils.getWeekdayCN(currentWeekday)}"
                    }

                    if (courses.isEmpty()) {
                        result.title = "暂无课程信息"
                        result.result = NextCourse.EMPTY_DATA
                        return@map result
                    }

                    //获取当天课程
                    val todayCourses: MutableList<Course> = ArrayList()
                    for (course in courses) {
                        if (course.weekSet.contains(currentWeek) && course.weekday == currentWeekday) {
                            todayCourses.add(course)
                        }
                    }
                    todayCourses.sortWith(Comparator { o1, o2 -> o1.beginNode.compareTo(o2.beginNode) })
                    if (todayCourses.isEmpty()) {
                        result.title = "今天没课哦~"
                        result.result = NextCourse.NO_TODAY_COURSE
                        return@map result
                    }

                    //计算下一节是第几节课
                    val intTime: List<Int> = setting.beginTime
                    //将课程按节数排列
                    @SuppressLint("UseSparseArrays")
                    val courseMap: MutableMap<Int, Course> = HashMap()
                    for (course in todayCourses) {
                        for (i in course.beginNode..course.endNode) {
                            courseMap[i] = course
                        }
                    }
                    result.totalNode = courseMap.size

                    val c: Calendar = Calendar.getInstance()
                    val now = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE)
                    var node = 0
                    for ((i, course) in courseMap) {
                        node++
                        val intStartTime = intTime[i]
                        val intEndTime = if (intStartTime % 100 + setting.nodeLength >= 60) {
                            intStartTime + 100 - intStartTime % 100 + (intStartTime % 100 + setting.nodeLength % 100) % 60
                        } else {
                            intStartTime + setting.nodeLength
                        }
                        if (now < intEndTime) {
                            result.name = course.name
                            result.result = NextCourse.HAS_NEXT_COURSE
                            result.address = course.address ?: "无"
                            result.node = node
                            result.timeText = String.format(Locale.CHINA, "%d:%02d-%d:%02d",
                                    intStartTime / 100, intStartTime % 100, intEndTime / 100, intEndTime % 100)
                            if (now >= intStartTime) {
                                //上课中
                                result.title = "正在上："
                                result.result = NextCourse.IN_COURSE
                                result.lastText = calcNextCourseIntervalTime(now, intEndTime) + "后下课"
                            } else {
                                //即将上课
                                result.title = "下一节课："
                                result.result = NextCourse.HAS_NEXT_COURSE
                                result.lastText = calcNextCourseIntervalTime(now, intStartTime) + "后上课"
                            }
                            return@map result
                        }
                    }
                    if (result.title.isEmpty()) {
                        result.title = "今天${result.totalNode}节课都上完了"
                        result.result = NextCourse.NO_NEXT_COURSE
                    }
                    return@map result
                }
    }


    private fun calcNextCourseIntervalTime(start: Int, end: Int): String {
        val last = (end / 100 - start / 100) * 60 + (end % 100 - start % 100)
        return (if (last >= 60) "${last / 60}小时" else "") +
                (if (last % 60 != 0) "${last % 60}分钟" else "")
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