package cn.ifafu.ifafu.mvp.syllabus

import android.annotation.SuppressLint
import android.content.Intent
import cn.ifafu.ifafu.R.string
import cn.ifafu.ifafu.base.addDisposable
import cn.ifafu.ifafu.base.ifafu.BaseZFPresenter
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.view.syllabus.CourseBase
import io.reactivex.Observable
import java.text.ParseException

class SyllabusPresenter(view: SyllabusContract.View)
    : BaseZFPresenter<SyllabusContract.View, SyllabusContract.Model>(view, SyllabusModel(view.context)),
        SyllabusContract.Presenter {

    @SuppressLint("DefaultLocale")
    override fun onCreate() {
        val setting = mModel.syllabusSetting
        if (setting == null) {
            mView.openActivity(Intent(mView.context, LoginActivity::class.java))
            mView.killSelf()
            return
        }
        mView.setSyllabusSetting(mModel.syllabusSetting)
        updateSyllabusLocal()
    }

    override fun updateSyllabusNet() {
        addDisposable {
            mModel.coursesFromNet
                    .map(this::holidayChange)
                    .compose(RxUtils.ioToMain())
                    .doOnSubscribe { mView.showLoading() }
                    .doFinally { mView.hideLoading() }
                    .subscribe({ list ->
                        mView.setSyllabusDate(list)
                        mView.showMessage(string.syllabus_refresh_success)
                    }, this::onError)
        }
    }

    override fun updateSyllabusLocal() {
        addDisposable {
            Observable
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
        }
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
        val fromTo: MutableMap<Int, MutableMap<Int, Pair<Int, Int>?>> = mModel.holidayFromToMap

        //按周排列课程
        val courseArray: MutableList<MutableList<CourseBase>?> = ArrayList()
        for (i in 0 until 24) {
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

    override fun cancelLoading() {
        mCompDisposable.clear()
    }
}