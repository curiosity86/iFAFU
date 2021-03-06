package cn.ifafu.ifafu.ui.main.old_theme

import androidx.lifecycle.*
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import cn.ifafu.ifafu.ui.main.bean.ExamPreview
import cn.ifafu.ifafu.ui.main.bean.ScorePreview
import cn.ifafu.ifafu.ui.main.bean.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainOldViewModel(val repo: RepositoryImpl) : ViewModel() {

    val user: LiveData<User> = liveData {
        RepositoryImpl.user.getInUse()?.let { emit(it) }
    }
    val semester: LiveData<String> = liveData {
        val semester = RepositoryImpl.getNowSemester().toString()
        emit(semester)
    }
    val online = MutableLiveData<Boolean>()
    val weather = MutableLiveData<Weather>()
    val classPreview = MutableLiveData<ClassPreview>()
    val examsPreview = MutableLiveData<ExamPreview>()
    val scorePreview = RepositoryImpl.ScoreRt.getNow().map {
        ScorePreview.convert(it)
    } as MutableLiveData<ScorePreview>

    init {
        GlobalScope.launch(Dispatchers.IO) {
            online.postValue(true)
        }
    }

    fun updateClassPreview() = GlobalScope.launch(Dispatchers.IO) {
        val courses = repo.syllabus.getAll()
        val setting = repo.syllabus.getSetting()
        //调课信息
        val holidayFromToMap = repo.syllabus.getAdjustmentInfo()
        classPreview.postValue(ClassPreview.convert(courses, holidayFromToMap, setting))
    }

    fun updateExamsPreview() = GlobalScope.launch(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val semester = RepositoryImpl.getNowSemester()
        val exams = RepositoryImpl.exam.getAll(semester.yearStr, semester.termStr)
                .filter { it.startTime > now || it.startTime == 0L }
        examsPreview.postValue(ExamPreview.convert(exams))
    }

    fun updateWeather() = GlobalScope.launch {
        repo.getWeather("101230101").getOrNull()?.let {
            weather.postValue(it)
        }
    }

    fun updateScorePreview() = GlobalScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            val scores = RepositoryImpl.ScoreRt.fetchNow().data
            scorePreview.postValue(ScorePreview.convert(scores))
        }
    }
}