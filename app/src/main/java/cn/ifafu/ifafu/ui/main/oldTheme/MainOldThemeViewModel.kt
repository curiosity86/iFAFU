package cn.ifafu.ifafu.ui.main.oldTheme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.bean.Weather
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.ui.main.oldTheme.bean.ClassPreview
import cn.ifafu.ifafu.ui.main.oldTheme.bean.ExamPreview
import cn.ifafu.ifafu.ui.main.oldTheme.bean.Menu
import cn.ifafu.ifafu.ui.main.oldTheme.bean.ScorePreview
import cn.ifafu.ifafu.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainOldThemeViewModel(val repo: Repository) : ViewModel() {

    val user = MutableLiveData<User>()
    val online = MutableLiveData<Boolean>()
    val weather = MutableLiveData<Weather>()
    val semester = MutableLiveData<String>()
    val classPreview = MutableLiveData<ClassPreview>()
    val examsPreview = MutableLiveData<ExamPreview>()
    val scorePreview = MutableLiveData<ScorePreview>()

    init {
        GlobalScope.launch {
            online.postValue(true)
            val semester = with(Repository.getNowSemester()) {
                "${yearStr}学年第${termStr}学期"
            }
            this@MainOldThemeViewModel.semester.postValue(semester)
            user.postValue(Repository.user.getInUse())
        }
    }

    fun updateClassPreview() {
        GlobalScope.launch(Dispatchers.Default) {
            classPreview.postValue(getNextClass())
        }
    }

    fun updateExamsPreview() {
        GlobalScope.launch(Dispatchers.Default) {
            examsPreview.postValue(getNextExams())
        }
    }

    fun updateWeather() {
        GlobalScope.launch(Dispatchers.IO) {
            with(Repository.WeatherRt.fetch("101230101").data) {
                this@MainOldThemeViewModel.weather.postValue(this)
            }
        }
    }

    fun updateScorePreview() {
        GlobalScope.launch {
            //先使用数据库数据，再在后台更新成绩信息
            scorePreview.postValue(getScorePreviewFromDb())
//            scorePreview.postValue(getScorePreviewFromNet())
        }
    }

    private suspend fun getScorePreviewFromDb(): ScorePreview {
        val scores = Repository.ScoreRt.getNow()
        return if (scores.isEmpty()) {
            ScorePreview(hasInfo = false, message = "暂无考试成绩")
        } else {
            ScorePreview(hasInfo = true, text = "已出${scores.size}门成绩")
        }
    }

//    private suspend fun getScorePreviewFromNet(): ScorePreview {
//        try {
//            val scores = Repository.ScoreRt.fetchNow()
//            return if (scores.isEmpty()) {
//                ScorePreview(hasInfo = false, message = "暂无考试成绩")
//            } else {
//                ScorePreview(hasInfo = true, text = "已出${scores.size}门成绩")
//            }
//        }
//    }

    private suspend inline fun getNextClass(): ClassPreview {
        val courses = this.repo.syllabus.getAll()
        val setting = this.repo.syllabus.getSetting()
        var currentWeek = setting.getCurrentWeek()
        var currentWeekday: Int = DateUtils.getCurrentWeekday()
        //计算节假日
        val holidayFromToMap = this.repo.syllabus.getAdjustmentInfo()
        if (holidayFromToMap[currentWeek]?.containsKey(currentWeekday) == true) {
            currentWeek = -1
        } else {
            for1@ for ((week, pair) in holidayFromToMap) {
                for ((weekday, pair2) in pair) {
                    if (pair2 != null && pair2.first == currentWeek && pair2.second == currentWeekday) {
                        currentWeek = week
                        currentWeekday = weekday
                        break@for1
                    }
                }
            }
        }
        var dateText = ""
        val date = SimpleDateFormat("MM月dd日", Locale.CHINA).format(Date())
        //当前周不在读书范围期间，则提示放假了
        if (currentWeek <= 0 || currentWeek > 20) {
            dateText = "放假中 $date ${DateUtils.getWeekdayCN(currentWeekday)}"
            return ClassPreview(hasInfo = false, message = "放假了呀！！", dateText = dateText)
        } else {
            dateText = "第${currentWeek}周 $date ${DateUtils.getWeekdayCN(currentWeekday)}"
        }
        //当数据库中课程信息为空，则提示无信息
        if (courses.isEmpty()) {
            return ClassPreview(hasInfo = false, message = "暂无课程信息", dateText = dateText)
        }
        //获取当天课程
        val todayCourses: MutableList<Course> = ArrayList()
        for (course in courses) {
            if (course.weekSet.contains(currentWeek) && course.weekday == currentWeekday) {
                todayCourses.add(course)
            }
        }
        todayCourses.sortWith(Comparator { o1, o2 -> o1.beginNode.compareTo(o2.beginNode) })
        if (todayCourses.isEmpty()) {
            return ClassPreview(hasInfo = false, message = "今天没课哦~", dateText = dateText)
        }

        //计算下一节是第几节课
        val intTime: List<Int> = setting.beginTime
        //将课程按节数排列
        val courseMap: MutableMap<Int, Course> = HashMap()
        for (course in todayCourses) {
            for (i in course.beginNode..course.endNode) {
                courseMap[i] = course
            }
        }
        val totalNode = courseMap.size //上课总节数
        val c: Calendar = Calendar.getInstance()
        val now = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE)
        var currentNode = 0 //当前上课节数
        var courseInfo: Course? = null
        var classTime = 0
        var afterClassTime = 0
        for ((i, course) in courseMap) {
            currentNode++
            classTime = intTime[i]
            afterClassTime = if (classTime % 100 + setting.nodeLength >= 60) {
                classTime + 100 - classTime % 100 + (classTime % 100 + setting.nodeLength % 100) % 60
            } else {
                classTime + setting.nodeLength
            }
            if (now < afterClassTime) {
                courseInfo = course
                break
            }
        }
        if (courseInfo != null) {
            val classTimeText = String.format(Locale.CHINA, "%d:%02d-%d:%02d",
                    classTime / 100, classTime % 100, afterClassTime / 100, afterClassTime % 100)
            val isInClass: Boolean
            var timeLeft = ""
            if (now >= classTime) {
                timeLeft = calcIntervalTimeForNextClass(now, afterClassTime) + "后下课"
                isInClass = true
            } else {
                timeLeft = calcIntervalTimeForNextClass(now, classTime) + "后上课"
                isInClass = false
            }
            return ClassPreview(
                    hasInfo = true,
                    nextClassName = courseInfo.name,
                    address = courseInfo.address,
                    numberOfClasses = arrayOf(currentNode, totalNode),
                    isInClass = isInClass,
                    classTime = classTimeText,
                    timeLeft = timeLeft,
                    dateText = dateText
            )
        }
        return ClassPreview(
                hasInfo = false,
                message = "今天${totalNode}节课都上完了",
                dateText = dateText
        )
    }

    private fun calcIntervalTimeForNextClass(start: Int, end: Int): String {
        val last = (end / 100 - start / 100) * 60 + (end % 100 - start % 100)
        var result = ""
        if (last >= 60) {
            result += "${last / 60}小时"
        }
        if (last % 60 != 0) {
            result += "${last % 60}分钟"
        }
        return result
    }

    private suspend inline fun getNextExams(): ExamPreview {
        val now = System.currentTimeMillis()
        val semester = Repository.getNowSemester()
        val thisTermExams = Repository.exam.getAll(semester.yearStr, semester.termStr)
                .filter { it.startTime > now || it.startTime == 0L }
        val list = ArrayList<ExamPreview.Item>()
        val max = if (thisTermExams.size < 2) thisTermExams.size else 2
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.CHINA)
        for (i in 0 until max) {
            var time: String
            var last: Array<String>
            if (thisTermExams[i].startTime == 0L) {
                time = "暂无考试时间"
                last = arrayOf("", "")
            } else {
                time = dateFormat.format(Date(thisTermExams[i].startTime)) +
                        "(${timeFormat.format(Date(thisTermExams[i].startTime))}" +
                        "-" +
                        "${timeFormat.format(Date(thisTermExams[i].endTime))})"
                last = calcIntervalTimeForNextExam(now, thisTermExams[i].startTime)
            }
            list.add(ExamPreview.Item(
                    examName = thisTermExams[i].name,
                    examTime = time,
                    address = thisTermExams[i].address,
                    seatNumber = thisTermExams[i].seatNumber,
                    timeLeftAndUnit = last
            ))
        }
        return if (list.isEmpty()) {
            ExamPreview(false, "暂无考试信息")
        } else {
            ExamPreview(
                    hasInfo = true,
                    items = arrayOf(list.getOrNull(0), list.getOrNull(1))
            )
        }
    }

    private fun calcIntervalTimeForNextExam(start: Long, end: Long): Array<String> {
        val second = (end - start) / 1000
        return when {
            second >= (24 * 60 * 60) ->
                arrayOf("${second / (24 * 60 * 60)}", "天")
            second >= 60 * 60 ->
                arrayOf("${second / (60 * 60)}", "小时")
            else ->
                arrayOf("${second / 60}", "分钟")
        }
    }
}