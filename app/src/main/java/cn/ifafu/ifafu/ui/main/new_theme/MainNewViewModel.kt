package cn.ifafu.ifafu.ui.main.new_theme

import android.app.Application
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.bean.Weather
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import cn.ifafu.ifafu.ui.main.new_theme.view.TimeEvent
import cn.ifafu.ifafu.util.DateUtils
import cn.woolsen.easymvvm.livedata.LiveDataField
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainNewViewModel(application: Application) : BaseViewModel(application) {

    val weather = LiveDataField<Weather>()
    val user = LiveDataField<User>()
    val timeEvents = LiveDataField<List<TimeEvent>>()
    val nextCourse = LiveDataField<ClassPreview>()

    private val repo = RepositoryImpl

    init {
        GlobalScope.launch {
            user.postValue(repo.user.getInUse())
        }
    }

    fun updateWeather() = GlobalScope.launch {
        repo.getWeather("101230101").getOrNull()?.let {
            weather.postValue(it)
        }
    }

    fun updateTimeAxis() = safeLaunchWithMessage {
        val list = ArrayList<TimeEvent>()
        val now = Date()
        val holidays = repo.syllabus.getHoliday()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        for (holiday in holidays) {
            val date = format.parse(holiday.date)
            val day = DateUtils.calcLastDays(now, date)
            if (day >= 0) {
                val top = if (day == 0) {
                    "${holiday.name} 今天"
                } else {
                    "${holiday.name} ${day}天"
                }
                val event = TimeEvent(holiday.date, top)
                list.add(event)
            }
        }
        val exams = repo.exam.getNow()
        list.addAll(exams.toTimeEvents())
        list.sortWith(Comparator { o1, o2 -> o1.bottom.compareTo(o2.bottom) })
        timeEvents.postValue(list)
        repo.getNotExamsFromDbOrNet().getOrFailure {
            toast(it.errorMessage())
        }?.let {
            list.addAll(it.toTimeEvents())
            timeEvents.postValue(list)
        }
    }

    fun updateNextCourse() = GlobalScope.launch {
        kotlin.runCatching {
            nextCourse.postValue(getNextCourse())
        }
    }

    private suspend fun getNextCourse(): ClassPreview {
        val courses = this.repo.syllabus.getAll()
        val setting = this.repo.syllabus.getSetting()
        //调课信息
        val holidayFromToMap = this.repo.syllabus.getAdjustmentInfo()
        return ClassPreview.convert(courses, holidayFromToMap, setting)
    }

    private fun List<Exam>.toTimeEvents(): List<TimeEvent> {
        val events = ArrayList<TimeEvent>()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val now = Date()
        forEach { exam ->
            if (exam.startTime != 0L) { //暂无时间信息
                val date = Date(exam.startTime)
                val day = DateUtils.calcLastDays(now, date)
                if (day >= 0) {
                    val bottom = if (day == 0) {
                        "${exam.name} 今天"
                    } else {
                        "${exam.name} ${day}天"
                    }
                    val axis = TimeEvent(format.format(Date(exam.startTime)), bottom)
                    events.add(axis)
                }
            }
        }
        return events
    }
}