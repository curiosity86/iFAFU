package cn.ifafu.ifafu.mvp.main

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.CookieManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.entity.NextCourse
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.view.timeline.TimeAxis
import com.tencent.bugly.beta.Beta
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import kotlin.Comparator

class MainPresenter internal constructor(view: MainContract.View) : BaseZFPresenter<MainContract.View, MainContract.Model>(view, MainModel(view.context)), MainContract.Presenter {

    override fun onCreate() {
        mView.setLeftMenuHeadName(mModel.userName)
        mView.setLeftMenuHeadIcon(mModel.schoolIcon)
        // 获取主页菜单
        mCompDisposable.add(mModel.menus
                .compose(RxUtils.ioToMain())
                .subscribe({ menus -> mView.setMenuAdapterData(menus) }, { this.onError(it) })
        )
        updateWeather()
        updateTimeLine()
        updateNextCourseView()
    }

    override fun updateApp() {
        mCompDisposable.add(Observable
                .fromCallable {
                    val info = Beta.getUpgradeInfo()
                    info != null && info.versionCode > GlobalLib.getLocalVersionCode(mView.activity)
                }
                .compose(RxUtils.ioToMain())
                .subscribe({ aBoolean ->
                    if (aBoolean!!) {
                        Beta.checkUpgrade()
                    } else {
                        mView.showMessage(R.string.is_last_version)
                    }
                }, { this.onError(it) })
        )
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
        if (mView.activity.intent.getIntExtra("come_from", -1) != 0) {
            IFAFU.loginDisposable = mModel.reLogin()
                    .compose(RxUtils.ioToMain())
                    .subscribe({ _ -> }, { this.onError(it) })
        }
        updateWeather()
        updateTimeLine()
        updateNextCourseView()
    }

    override fun updateWeather() {
        // 获取天气
        mCompDisposable.add(mModel.getWeather("101230101")
                .compose(RxUtils.ioToMain())
                .subscribe({ weather -> mView.setWeatherText(weather) }, { this.onError(it) })
        )
    }

    @SuppressLint("DefaultLocale")
    override fun updateNextCourseView() {
        mCompDisposable.add(Observable
                .fromCallable {
                    val model = SyllabusModel(mView.context)
                    model.nextCourse
                }
                .compose(RxUtils.computationToMain())
                .subscribe({ next ->
                    when (next.result) {
                        NextCourse.IN_HOLIDAY, NextCourse.EMPTY_DATA, NextCourse.NO_TODAY_COURSE, NextCourse.NO_NEXT_COURSE -> mView.setCourseText(next.title, "", "", "")
                        NextCourse.HAS_NEXT_COURSE -> mView.setCourseText(next.title, next.name, next.address, next.timeText)
                    }
                }, { throwable ->
                    onError(throwable)
                    mView.setCourseText("获取课程信息失败", "", "", "")
                })
        )
    }

    override fun updateTimeLine() {
        mCompDisposable.add(Observable
                .fromCallable<List<TimeAxis>> {
                    val list = ArrayList<TimeAxis>()
                    val now = Date()

                    val holidays = mModel.holiday
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

                    val exams = mModel.thisTermExams
                    for (exam in exams) {
                        val date = Date(exam.startTime)
                        val day = DateUtils.calcLastDays(now, date)
                        if (day >= 0) {
                            val axis = TimeAxis(
                                    exam.name, format.format(Date(exam.startTime)), day)
                            list.add(axis)
                        }
                    }
                    list.sortWith(Comparator { o1, o2 -> Integer.compare(o1.day, o2.day) })
                    list.subList(0, if (list.size < 4) list.size else 4)
                }
                .compose(RxUtils.ioToMain())
                .subscribe({ list -> mView.setTimeLineData(list) }, { this.onError(it) })
        )
    }

    override fun quitAccount() {
        CookieManager.getInstance().removeAllCookies(null)
        val intent = Intent(mView.context, LoginActivity::class.java)
        mModel.clearAllDate()
        mView.openActivity(intent)
        mView.killSelf()
    }
}
