package cn.ifafu.ifafu.mvp.syllabus

import android.annotation.SuppressLint
import android.util.Log
import cn.ifafu.ifafu.R.string
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.view.syllabus.CourseBase
import io.reactivex.Observable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SyllabusPresenter(view: SyllabusContract.View)
    : BaseZFPresenter<SyllabusContract.View, SyllabusContract.Model>(view, SyllabusModel(view.context)),
        SyllabusContract.Presenter {

    @SuppressLint("DefaultLocale")
    override fun onCreate() {
        updateSyllabusLocal()
        updateSyllabusSetting()
    }

    override fun updateSyllabusNet() {
        mCompDisposable.add(mModel.coursesFromNet
                .map(this::holidayChange)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe { mView.showLoading() }
                .doFinally { mView.hideLoading() }
                .subscribe({ list ->
                    mView.setSyllabusDate(list)
                    mView.showMessage(string.syllabus_refresh_success)
                }, this::onError)
        )
    }

    override fun updateSyllabusLocal() {
        mCompDisposable.add(Observable
                .fromCallable { mModel.allCoursesFromDB }
                .flatMap { o: List<Course> ->
                    if (o.isEmpty()) {
                        mModel.coursesFromNet
                    } else {
                        Observable.just(o)
                    }
                }
                .map(this::holidayChange)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe { mView.showLoading() }
                .doFinally { mView.hideLoading() }
                .subscribe({ list ->
                    mView.setSyllabusDate(list)
                }, this::onError)
        )
    }

    override fun updateSyllabusSetting() {
        mCompDisposable.add(Observable
                .fromCallable { mModel.syllabusSetting }
                .compose(RxUtils.computationToMain())
                .subscribe({ set: SyllabusSetting ->
                    mView.setSyllabusSetting(set)
                }, this::onError)
        )
    }

    /**
     * 节假日调课，并按周排列课表
     * @return MutableList<MutableList<CourseBase>?> 分周排列课表
     * @throws ParseException
     */
    private fun holidayChange(oldCourses: List<Course>): MutableList<MutableList<CourseBase>?> {

        //MutableMap<fromWeek, MutableMap<fromWeekday, Pair<toWeek, toWeekday>>>
        @SuppressLint("UseSparseArrays")
        val fromTo: MutableMap<Int, MutableMap<Int, Pair<Int, Int>?>> = HashMap()
        val setting: SyllabusSetting = mModel.syllabusSetting
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val openingDate: Date = format.parse(mModel.syllabusSetting.openingDay)
        val calendar: Calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = setting.firstDayOfWeek
        for (holiday in  mModel.holidays) {
            if (holiday.fromTo != null) { //节假日需要调课
                for ((key, value) in holiday.fromTo) {

                    val fromDate: Date = format.parse(key)
                    val fromWeek = DateUtils.getCurrentWeek(openingDate, fromDate, setting.firstDayOfWeek)
                    calendar.time = fromDate
                    val fromWeekday = calendar.get(Calendar.DAY_OF_WEEK)
                    Log.d("Holiday Calc", "from week: $fromWeek, fromWeekday: $fromWeekday")

                    val toDate: Date = format.parse(value)
                    val toWeek = DateUtils.getCurrentWeek(openingDate, toDate, setting.firstDayOfWeek)
                    calendar.time = toDate
                    val toWeekday = calendar.get(Calendar.DAY_OF_WEEK)
                    val toPair = Pair(toWeek, toWeekday)

                    fromTo.getOrPut(fromWeek, { HashMap() })[fromWeekday] = toPair
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
                        fromTo.getOrPut(holidayWeek, { HashMap() }).run {
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

        Log.d("Holiday Calc", "fromTo: $fromTo")
        val courseArray: MutableList<MutableList<CourseBase>?> = ArrayList()
        for (i in 0 until setting.weekCnt) {
            courseArray.add(null)
        }
//        Log.d("Holiday Calc", "holiday: $fromTo")
        oldCourses.forEach { course ->
            course.weekSet.forEach { week ->
                var flag = true
                if (fromTo.containsKey(week)) {
                    val h = fromTo[week] ?: error("")
                    for ((weekday, weekAndWeekday) in h) {
                        if (weekday == course.weekday) {
                            if (weekAndWeekday != null) {
                                val c = course.toCourseBase()
                                c.text += "\n[补课]"
                                c.weekday = weekAndWeekday.second
                                if (courseArray[week - 1] == null) {
                                    courseArray[week - 1] = ArrayList()
                                }
                                courseArray[week - 1]!!.add(c)
                            }
                            flag = false
                            break
                        }
                    }
                }
                if (flag) {
                    if (courseArray[week - 1] == null) {
                        courseArray[week - 1] = ArrayList()
                    }
                    courseArray[week - 1]!!.add(course.toCourseBase())
                }
            }
        }
        return courseArray
    }

    override fun onDelete(course: Course) {
        mModel.deleteCourse(course)
        updateSyllabusLocal()
    }
}