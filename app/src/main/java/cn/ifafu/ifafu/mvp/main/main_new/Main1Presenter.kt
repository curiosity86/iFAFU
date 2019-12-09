package cn.ifafu.ifafu.mvp.main.main_new

import android.annotation.SuppressLint
import android.content.Intent
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.entity.NextCourse
import cn.ifafu.ifafu.mvp.main.BaseMainPresenter
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.view.timeline.TimeAxis
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

class Main1Presenter(view: Main1Contract.View)
    : BaseMainPresenter<Main1Contract.View, Main1Contract.Model>(view, Main1Model(view.context)), Main1Contract.Presenter {

    override fun onCreate() {
        super.onCreate()
        val user = mModel.getLoginUser()
        mView.setLeftMenuHeadName(user?.name ?: "Null")
        mView.setLeftMenuHeadIcon(mModel.getSchoolIcon())
        // 获取主页菜单
        mCompDisposable.add(mModel.getMenus()
                .compose(RxUtils.ioToMain())
                .subscribe({ menus -> mView.setMenuAdapterData(menus) }, this::onError)
        )
        updateWeather()
        updateTimeLine()
        updateNextCourseView()
    }

    override fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享")
        intent.putExtra(Intent.EXTRA_TEXT, "iFAFU下载链接：http://ifafu.cn")
        mView.openActivity(Intent.createChooser(intent, "分享"))
    }

    override fun onRefresh() {
        IFAFU.loginDisposable = mModel.reLogin()
                .compose(RxUtils.ioToMain())
                .subscribe({ }, this::onError)
        updateWeather()
        updateTimeLine()
        updateNextCourseView()
    }

    override fun updateWeather() {
        // 获取天气
        addDisposable {
            mModel.getWeather("101230101")
                    .map { Pair("${it.nowTemp}℃" + "", "${it.cityName} | ${it.weather}") }
                    .compose(RxUtils.ioToMain())
                    .subscribe({ mView.setWeatherText(it) }, this::onError)
        }
    }

    @SuppressLint("DefaultLocale")
    override fun updateNextCourseView() {
        addDisposable {
            mModel.getNextCourse()
                    .compose(RxUtils.computationToMain())
                    .subscribe({ next ->
                        when (next.result) {
                            NextCourse.IN_HOLIDAY,
                            NextCourse.EMPTY_DATA,
                            NextCourse.NO_TODAY_COURSE,
                            NextCourse.NO_NEXT_COURSE -> {
                                mView.setCourseText(next.title, "", "", "")
                            }
                            NextCourse.HAS_NEXT_COURSE,
                            NextCourse.IN_COURSE -> {
                                mView.setCourseText(next.title, next.name, next.address + "   " + next.timeText, next.lastText)
                            }
                        }
                    }, { throwable ->
                        onError(throwable)
                        mView.setCourseText("获取课程信息失败", "", "", "")
                    })
        }
    }

    override fun updateTimeLine() {
        addDisposable {
            Observable
                    .fromCallable<List<TimeAxis>> {
                        val list = ArrayList<TimeAxis>()
                        val now = Date()

                        val holidays = mModel.getHoliday()
                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                        for (holiday in holidays) {
                            val date = format.parse(holiday.date)
                            val day = DateUtils.calcLastDays(now, date)
                            if (day >= 0) {
                                val axis = TimeAxis(
                                        holiday.name, holiday.date, day)
                                list.add(axis)
                            }
                        }

                        val exams = mModel.getThisTermExams()
                        for (exam in exams) {
                            println("start time: ${exam.startTime}")
                            if (exam.startTime == 0L) { //暂无时间信息
                                continue
                            }
                            val date = Date(exam.startTime)
                            val day = DateUtils.calcLastDays(now, date)
                            if (day >= 0) {
                                val axis = TimeAxis(
                                        exam.name, format.format(Date(exam.startTime)), day)
                                list.add(axis)
                            }
                        }
                        list.sortWith(Comparator { o1, o2 -> o1.day.compareTo(o2.day) })
                        list
                    }
                    .compose(RxUtils.ioToMain())
                    .subscribe({ list -> mView.setTimeLineData(list) }, this::onError)
        }
    }

}
