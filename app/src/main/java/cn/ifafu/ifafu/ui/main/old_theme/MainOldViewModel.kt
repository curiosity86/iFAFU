package cn.ifafu.ifafu.ui.main.old_theme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.ifafu.ifafu.data.bean.Weather
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import cn.ifafu.ifafu.ui.main.bean.ExamPreview
import cn.ifafu.ifafu.ui.main.bean.ScorePreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainOldViewModel(val repo: RepositoryImpl) : ViewModel() {

    val user = MutableLiveData<User>()
    val online = MutableLiveData<Boolean>()
    val weather = MutableLiveData<Weather>()
    val semester = MutableLiveData<String>()
    val classPreview = MutableLiveData<ClassPreview>()
    val examsPreview = MutableLiveData<ExamPreview>()
    val scorePreview = MutableLiveData<ScorePreview>()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            online.postValue(true)
            val semester = RepositoryImpl.getNowSemester().toString()
            this@MainOldViewModel.semester.postValue(semester)
            updateScorePreviewFromDb()
            user.postValue(RepositoryImpl.user.getInUse())
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

    fun updateWeather() = GlobalScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            with(RepositoryImpl.WeatherRt.fetch("101230101").data) {
                this@MainOldViewModel.weather.postValue(this)
            }
        }
    }

    fun updateScorePreview() = GlobalScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            val scores = RepositoryImpl.ScoreRt.fetchNow().data
            scorePreview.postValue(ScorePreview.convert(scores))
        }
    }

    private suspend fun updateScorePreviewFromDb() = GlobalScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            val db = RepositoryImpl.ScoreRt.getNow()
            scorePreview.postValue(ScorePreview.convert(db))
        }
    }

}