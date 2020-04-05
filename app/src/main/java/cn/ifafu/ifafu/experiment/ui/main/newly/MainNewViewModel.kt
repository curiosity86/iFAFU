package cn.ifafu.ifafu.experiment.ui.main.newly

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import cn.ifafu.ifafu.ui.main.bean.Weather
import cn.ifafu.ifafu.ui.main.new_theme.view.TimeEvent
import cn.ifafu.ifafu.util.DateUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainNewViewModel(application: Application) : BaseViewModel(application) {

    private val repo = RepositoryImpl

    val weather = MutableLiveData<Weather>()
    val user: LiveData<User> = liveData {
        repo.user.getInUse()?.let { emit(it) }
    }
    val timeEvents = MutableLiveData<List<TimeEvent>>()
    val nextCourse = MutableLiveData<ClassPreview>()

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
                val event = TimeEvent(date?.time ?: 0L, top)
                list.add(event)
            }
        }
        val exams = repo.exam.getNow()
        list.addAll(exams.toTimeEvents())
        list.sortWith(Comparator { o1, o2 -> o1.text.compareTo(o2.text) })
        timeEvents.postValue(list)
        repo.getNotExamsFromDbOrNet().getOrFailure {
            toast(it.errorMessage())
        }?.let {
            list.addAll(it.toTimeEvents())
            list.sortWith(Comparator { o1, o2 -> o1.text.compareTo(o2.text) })
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
        val holidayFromToMap = this.repo.syllabus.getAdjustmentInfo()
        return ClassPreview.convert(courses, holidayFromToMap, setting)
    }

    private fun List<Exam>.toTimeEvents(): List<TimeEvent> {
        val events = ArrayList<TimeEvent>()
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
                    val axis = TimeEvent(exam.startTime, bottom)
                    events.add(axis)
                }
            }
        }
        return events
    }
}