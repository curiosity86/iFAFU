package cn.ifafu.ifafu.mvp.syllabus

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
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

class SyllabusModel(context: Context) : BaseZFModel(context), Model {

    override fun getSyllabusSetting(): SyllabusSetting {
        return repository.getSyllabusSetting()
    }

    override fun getCurrentWeek(): Int {
        return try {
            val setting = syllabusSetting
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            val firstStudyDate: Date = format.parse(setting.openingDay)
            val calendar: Calendar = Calendar.getInstance()
            calendar.firstDayOfWeek = setting.firstDayOfWeek
            val currentYearWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            calendar.time = firstStudyDate
            val firstYearWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            val nowWeek = currentYearWeek - firstYearWeek + 1
            if (nowWeek > 0) nowWeek else -1
        } catch (e: Exception) {
            -1
        }
    }

    override fun getAllCoursesFromDB(): List<Course> {
        return repository.getAllCourses()
    }

    override fun getHolidays(): List<Holiday> {
        return listOf(
                Holiday("教师节", "2019-09-10", 0),
                Holiday("中秋节", "2019-09-13", 3).apply {
                },
                Holiday("国庆节", "2019-10-01", 7).apply {
                    addFromTo("2019-10-04", "2019-09-29")
                    addFromTo("2019-10-07", "2019-10-12")
                },
                Holiday("双十一", "2019-11-11", 0),
                Holiday("圣诞节", "2019-12-25", 0),
                Holiday("元旦", "2020-01-01", 3),
                Holiday("春节", "2020-01-25", 0),
                Holiday("清明节", "2020-04-04", 3),
                Holiday("劳动节", "2020-05-01", 3)
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
            val url: String = School.getUrl(ZhengFang.SYLLABUS, user) ?: ""
            val referer: String = School.getUrl(ZhengFang.MAIN, user) ?: ""
            initParams(url, referer)
                    .flatMap {
                        val type = repository.getSyllabusSetting().parseType
                        if (type == 1) {
                            APIManager.getZhengFangAPI()
                                    .getInfo(url, referer)
                                    .compose(SyllabusParser(user))
                        } else {
                            val html = APIManager.getZhengFangAPI().getInfo(url, referer).blockingFirst().string()
                            APIManager.getZhengFangAPI()
                                    .parse("http://woolsen.cn:8080/syllabus", html)
                                    .compose(SyllabusParser2(user))
                                    .map { Response.success(it) }
                        }
                    }
        }
                .doOnNext { response ->
                    val courses = response.body ?: ArrayList()
                    if (courses.isNotEmpty()) {
                        repository.deleteAllOnlineCourse()
                        repository.saveCourse(courses)
                        //获取校区信息（影响上课时间）
                        val setting = repository.getSyllabusSetting()
                        var qs = false
                        for (course in repository.getAllCourses()) {
                            if (course.address.contains("旗教")) {
                                qs = true
                                break
                            }
                        }
                        setting.beginTime = (if (qs) SyllabusSetting.intBeginTime[1] else SyllabusSetting.intBeginTime[0]).toList()
                        repository.saveSyllabusSetting(setting)
                        courses.addAll(repository.getCourses(true))
                    } else {
                        //获取课表为空则调取数据库完整课表
                        courses.addAll(repository.getAllCourses())
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