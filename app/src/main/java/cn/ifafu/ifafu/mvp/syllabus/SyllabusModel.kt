package cn.ifafu.ifafu.mvp.syllabus

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.mvp.BaseModel
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.SyllabusParser
import cn.ifafu.ifafu.data.http.parser.SyllabusParser2
import cn.ifafu.ifafu.entity.*
import cn.ifafu.ifafu.mvp.syllabus.SyllabusContract.Model
import cn.ifafu.ifafu.util.DateUtils
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SyllabusModel(context: Context) : BaseModel(context), Model {

    override fun getSyllabusSetting(): SyllabusSetting {
        return mRepository.getSyllabusSetting()
    }

    override fun getAllCoursesFromDB(): List<Course> {
        return mRepository.getAllCourses()
    }

    override fun getHolidays(): List<Holiday> {
        return listOf(
                Holiday("元旦", "2020-01-01", 1),
                Holiday("春节", "2020-01-25", 0),
                Holiday("开始上课", "2020-02-17", 0),
                Holiday("清明节", "2020-04-04", 3),
                Holiday("劳动节", "2020-05-01", 5).apply {
                    addFromTo("2020-05-05", "2019-05-09")
                },
                Holiday("端午节", "2020-06-25", 3).apply {
                    addFromTo("2020-06-26", "2019-06-28")
                }
        )
    }

    /**
     * 获取调课方式
     * Map<fromWeek+fromWeekday, toWeek+toWeekday>
     * @return MutableMap<fromWeek, MutableMap<fromWeekday, Pair<toWeek, toWeekday>>>
     *     把(fromWeek,fromWeekday)的课调到(toWeek,toWeekday)
     *     放假则Pair<toWeek, toWeekday>为null
     */
    override fun getHolidayFromToMap(): Map<Int, Map<Int, Pair<Int, Int>?>> {
        //MutableMap<fromWeek, MutableMap<fromWeekday, Pair<toWeek, toWeekday>>>
        @SuppressLint("UseSparseArrays")
        val fromToMap: MutableMap<Int, MutableMap<Int, Pair<Int, Int>?>> = HashMap()
        val setting: SyllabusSetting = syllabusSetting
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val openingDate: Date = format.parse(syllabusSetting.openingDay)
        val calendar: Calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = setting.firstDayOfWeek
        for (holiday in holidays) {
            if (holiday.fromTo != null) { //节假日需要调课
                for ((key, value) in holiday.fromTo) {
                    val fromDate: Date = format.parse(key)
                    val fromWeek = DateUtils.getCurrentWeek(openingDate, fromDate, setting.firstDayOfWeek)
                    calendar.time = fromDate
                    val fromWeekday = calendar.get(Calendar.DAY_OF_WEEK)
//                    Log.d("Holiday Calc", "from week: $fromWeek, fromWeekday: $fromWeekday")
                    val toDate: Date = format.parse(value)
                    val toWeek = DateUtils.getCurrentWeek(openingDate, toDate, setting.firstDayOfWeek)
                    calendar.time = toDate
                    val toWeekday = calendar.get(Calendar.DAY_OF_WEEK)
                    val toPair = Pair(toWeek, toWeekday)
                    fromToMap.getOrPut(fromWeek, { HashMap() })[fromWeekday] = toPair
                }
            }
            //添加放假日期
            if (holiday.day != 0) {
                val holidayDate: Date = format.parse(holiday.date)
                calendar.time = holidayDate
//                Log.d("Holiday Calc", "holiday date: ${holiday.date}    day: ${holiday.day}天")
                for (i in 0 until holiday.day) {
                    val holidayWeek = DateUtils.getCurrentWeek(openingDate, calendar.time, setting.firstDayOfWeek)
//                    Log.d("Holiday Calc", "holiday week = $holidayWeek    $i")
                    if (holidayWeek <= setting.weekCnt) {
                        fromToMap.getOrPut(holidayWeek, { HashMap() }).run {
                            val weekday = calendar.get(Calendar.DAY_OF_WEEK)
                            if (!this.containsKey(weekday)) {
                                this[weekday] = null
                            }
                        }

                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
            }
        }
        return fromToMap
    }

    override fun getCoursesFromNet(): Observable<Response<MutableList<Course>>> {
        return Observable.fromCallable { getUser() }.flatMap { user ->
            val url: String = School.getUrl(ZFApiList.SYLLABUS, user) ?: ""
            val referer: String = School.getUrl(ZFApiList.MAIN, user) ?: ""
            initParams(url, referer)
                    .flatMap {
                        val type = mRepository.getSyllabusSetting().parseType
                        if (type == 1) {
                            APIManager.zhengFangAPI
                                    .getInfo(url, referer)
                                    .compose(SyllabusParser(user))
                        } else {
                            val html = APIManager.zhengFangAPI.getInfo(url, referer).blockingFirst().string()
                            APIManager.zhengFangAPI
                                    .parse("http://woolsen.cn:8080/syllabus", html)
                                    .compose(SyllabusParser2(user))
                                    .map { Response.success(it) }
                        }
                    }
        }
                .doOnNext { response ->
                    val courses = response.body ?: ArrayList()
                    if (courses.isNotEmpty()) {
                        mRepository.deleteAllOnlineCourse()
                        mRepository.saveCourse(courses)
                        //获取校区信息（影响上课时间）
                        val setting = mRepository.getSyllabusSetting()
                        var qs = false
                        for (course in mRepository.getAllCourses()) {
                            if (course.address.contains("旗教")) {
                                qs = true
                                break
                            }
                        }
                        setting.beginTime = (if (qs) SyllabusSetting.intBeginTime[1] else SyllabusSetting.intBeginTime[0]).toList()
                        mRepository.saveSyllabusSetting(setting)
                        courses.addAll(mRepository.getCourses(true))
                    } else {
                        //获取课表为空则调取数据库完整课表
                        courses.addAll(mRepository.getAllCourses())
                    }
                }
    }

    override fun getCoursesFromDB(week: Int, weekday: Int): List<Course> {
        val todayCourses: MutableList<Course> = ArrayList()
        for (course in allCoursesFromDB) {
            if (course.weekSet.contains(week) && course.weekday == weekday) {
                todayCourses.add(course)
            }
        }
        return todayCourses
    }

}