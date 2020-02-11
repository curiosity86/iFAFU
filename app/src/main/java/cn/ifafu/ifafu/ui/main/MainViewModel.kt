package cn.ifafu.ifafu.ui.main

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.ui.electricity.ElectricityActivity
import cn.ifafu.ifafu.ui.elective.ElectiveActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.score_list.ScoreListActivity
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import cn.ifafu.ifafu.ui.web.WebActivity
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.view.timeline.TimeAxis
import com.alibaba.fastjson.JSONObject
import com.tencent.bugly.beta.Beta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : BaseViewModel(application) {

    val users by lazy { MutableLiveData<List<User>>() }
    val theme by lazy { MutableLiveData<Int>() }
    val isShowSwitchAccountDialog by lazy { MutableLiveData<Boolean>() }

    val nextCourse by lazy { MutableLiveData<NextCourse>() }
    val weather by lazy { MutableLiveData<Weather>() }
    val nextExam by lazy { MutableLiveData<List<NextExam>>() }

    /**
     * 旧版主题
     */
    val inUseUser by lazy { MutableLiveData<User>() }
    val oldThemeMenu by lazy { MutableLiveData<List<Pair<String, List<Pair<String, Int>>>>>() }
    val onlineStatus by lazy { MutableLiveData<Boolean>() }
    val semesterTitle by lazy { MutableLiveData<String>() }
    val scores by lazy { MutableLiveData<List<Score>>() } //post null value 则出错

    /**
     * 新版主题
     */
    val newThemeMenu by lazy { MutableLiveData<List<Menu>>() }
    val timeAxis by lazy { MutableLiveData<List<TimeAxis>>() }
    val schoolIcon by lazy { MutableLiveData<Drawable>() }

    /**
     * 初始化所有数据
     */
    fun initActivityData() {
        checkTheme()
    }

    fun initFragmentData() {
        safeLaunch {
            val user = Repository.user.getInUse()
            withContext(Dispatchers.Main) {
                inUseUser.value = user
            }
            when (theme.value) {
                GlobalSetting.THEME_NEW -> {
                    initNewTabMenu()
                }
                GlobalSetting.THEME_OLD -> {
                    initOldTabMenu()
                    safeLaunch {
                        val semester = Repository.getNowSemester()
                        semesterTitle.postValue("${semester.yearStr}学年第${semester.termStr}学期")
                        onlineStatus.postValue(true)
                    }
                }
            }
        }
    }

    private fun initNewTabMenu() {
        safeLaunch {
            newThemeMenu.postValue(
                    listOf(
                            Menu(getApplication<Application>().getDrawable(R.drawable.main_menu_tabs_syllabus)!!, "课程表", SyllabusActivity::class.java),
                            Menu(getApplication<Application>().getDrawable(R.drawable.main_menu_tabs_exam)!!, "考试计划", ExamListActivity::class.java),
                            Menu(getApplication<Application>().getDrawable(R.drawable.main_menu_tabs_score)!!, "成绩查询", ScoreListActivity::class.java),
                            Menu(getApplication<Application>().getDrawable(R.drawable.menu_elective)!!, "选修学分查询", ElectiveActivity::class.java),
                            Menu(getApplication<Application>().getDrawable(R.drawable.main_menu_tabs_web)!!, "网页模式", WebActivity::class.java),
                            Menu(getApplication<Application>().getDrawable(R.drawable.main_menu_tabs_electricity)!!, "电费查询", ElectricityActivity::class.java),
//                            Menu(getApplication<Application>().getDrawable(R.drawable.main_menu_tabs_comment)!!, "一键评教", ::class.java),
                            Menu(getApplication<Application>().getDrawable(R.drawable.main_menu_tabs_repair)!!, "报修服务", WebActivity::class.java)
                    )
            )
            schoolIcon.postValue(when (inUseUser.value?.schoolCode) {
                School.FAFU -> getApplication<Application>().getDrawable((R.drawable.fafu_bb_icon_white))
                School.FAFU_JS -> getApplication<Application>().getDrawable((R.drawable.fafu_js_icon_white))
                else -> getApplication<Application>().getDrawable((R.mipmap.ic_launcher_round))
            })
        }
    }

    private fun initOldTabMenu() {
        safeLaunch {
            oldThemeMenu.postValue(listOf(
                    Pair("信息查询", listOf(
                            Pair("成绩查询", R.drawable.menu_score_white),
                            Pair("学生考试查询", R.drawable.menu_exam_white),
                            Pair("电费查询", R.drawable.menu_elec_white),
                            Pair("选修学分查询", R.drawable.menu_elective_white))),
                    Pair("实用工具", listOf(
                            Pair("我的课表", R.drawable.menu_syllabus_white),
                            Pair("网页模式", R.drawable.menu_web_white),
                            Pair("报修服务", R.drawable.main_old_tabs_repair),
                            Pair("一键评教", R.drawable.main_old_tabs_comment))),
                    Pair("软件设置", listOf(
                            Pair("软件设置", R.drawable.menu_setting_white),
                            Pair("账号管理", R.drawable.main_old_tabs_manage))),
                    Pair("关于软件", listOf(
                            Pair("检查更新", R.drawable.menu_update_white),
                            Pair("关于iFAFU", R.drawable.main_old_tabs_about)))))
        }
    }

    fun updateNextCourse() {
        safeLaunch {
            nextCourse.postValue(getNextCourse())
        }
    }

    fun updateTimeAxis() {
        safeLaunch {
            val list = ArrayList<TimeAxis>()
            val now = Date()
            val holidays = Repository.syllabus.getHoliday()
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
            val exams = Repository.exam.getNow()
            val toTimeAxis: (List<Exam>) -> List<TimeAxis> = {
                val timeAxises = ArrayList<TimeAxis>()
                for (exam in it) {
                    if (exam.startTime == 0L) { //暂无时间信息
                        continue
                    }
                    val date = Date(exam.startTime)
                    val day = DateUtils.calcLastDays(now, date)
                    if (day >= 0) {
                        val axis = TimeAxis(
                                exam.name, format.format(Date(exam.startTime)), day)
                        timeAxises.add(axis)
                    }
                }
                timeAxises
            }
            list.addAll(toTimeAxis(exams))
            list.sortWith(Comparator { o1, o2 -> o1.day.compareTo(o2.day) })
            timeAxis.postValue(list)
            Repository.exam.fetchNow().data?.run {
                list.addAll(toTimeAxis(this))
                timeAxis.postValue(list)
            }
        }
    }

    private suspend inline fun getNextCourse(): NextCourse {
        val result = NextCourse()
        val courses = Repository.syllabus.getAll()
        val setting = Repository.syllabus.getSetting()
        var currentWeek = setting.getCurrentWeek()
        var currentWeekday: Int = DateUtils.getCurrentWeekday()
        val date = SimpleDateFormat("MM月dd日", Locale.CHINA).format(Date())
        //计算节假日
        val holidayFromToMap = Repository.syllabus.getAdjustmentInfo()
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
        if (currentWeek <= 0 || currentWeek > 20) {
            result.title = "放假了呀！！"
            result.result = NextCourse.IN_HOLIDAY
            result.dateText = "放假中 $date ${DateUtils.getWeekdayCN(currentWeekday)}"
            return result
        } else {
            result.dateText = "第${currentWeek}周 $date ${DateUtils.getWeekdayCN(currentWeekday)}"
        }

        if (courses.isEmpty()) {
            result.title = "暂无课程信息"
            result.result = NextCourse.EMPTY_DATA
            return result
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
            result.title = "今天没课哦~"
            result.result = NextCourse.NO_TODAY_COURSE
            return result
        }

        //计算下一节是第几节课
        val intTime: List<Int> = setting.beginTime
        //将课程按节数排列
        @SuppressLint("UseSparseArrays")
        val courseMap: MutableMap<Int, Course> = HashMap()
        for (course in todayCourses) {
            for (i in course.beginNode..course.endNode) {
                courseMap[i] = course
            }
        }
        result.totalNode = courseMap.size

        val c: Calendar = Calendar.getInstance()
        val now = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE)
        var node = 0
        for ((i, course) in courseMap) {
            node++
            val intStartTime = intTime[i]
            val intEndTime = if (intStartTime % 100 + setting.nodeLength >= 60) {
                intStartTime + 100 - intStartTime % 100 + (intStartTime % 100 + setting.nodeLength % 100) % 60
            } else {
                intStartTime + setting.nodeLength
            }
            if (now < intEndTime) {
                result.name = course.name
                result.result = NextCourse.HAS_NEXT_COURSE
                result.address = course.address
                result.node = node
                result.timeText = String.format(Locale.CHINA, "%d:%02d-%d:%02d",
                        intStartTime / 100, intStartTime % 100, intEndTime / 100, intEndTime % 100)
                if (now >= intStartTime) {
                    //上课中
                    result.title = "正在上："
                    result.result = NextCourse.IN_COURSE
                    result.lastText = calcNextCourseIntervalTime(now, intEndTime) + "后下课"
                } else {
                    //即将上课
                    result.title = "下一节课："
                    result.result = NextCourse.HAS_NEXT_COURSE
                    result.lastText = calcNextCourseIntervalTime(now, intStartTime) + "后上课"
                }
                return result
            }
        }
        if (result.title.isEmpty()) {
            result.title = "今天${result.totalNode}节课都上完了"
            result.result = NextCourse.NO_NEXT_COURSE
        }
        return result
    }

    private fun calcNextCourseIntervalTime(start: Int, end: Int): String {
        val last = (end / 100 - start / 100) * 60 + (end % 100 - start % 100)
        return (if (last >= 60) "${last / 60}小时" else "") +
                (if (last % 60 != 0) "${last % 60}分钟" else "")
    }

    fun updateExamInfo() {
        safeLaunch {
            val now = System.currentTimeMillis()
            val semester = Repository.getNowSemester()
            val thisTermExams = Repository.exam.getAll(semester.yearStr, semester.termStr)
                    .filter { it.startTime > now || it.startTime == 0L }
            val list = ArrayList<NextExam>()
            val max = if (thisTermExams.size < 2) thisTermExams.size else 2
            val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.CHINA)
            for (i in 0 until max) {
                var time: String
                var last: Pair<String, String>
                if (thisTermExams[i].startTime == 0L) {
                    time = "暂无考试时间"
                    last = Pair("", "")
                } else {
                    time = dateFormat.format(Date(thisTermExams[i].startTime)) +
                            "(${timeFormat.format(Date(thisTermExams[i].startTime))}" +
                            "-" +
                            "${timeFormat.format(Date(thisTermExams[i].endTime))})"
                    last = calcExamIntervalTime(now, thisTermExams[i].startTime)
                }
                list.add(NextExam(
                        name = thisTermExams[i].name,
                        time = time,
                        address = thisTermExams[i].address,
                        seatNum = thisTermExams[i].seatNumber,
                        last = last
                ))
            }
            nextExam.postValue(list)
        }

    }

    private fun calcExamIntervalTime(start: Long, end: Long): Pair<String, String> {
        val intervalSec = (end - start) / 1000
        return when {
            intervalSec >= (24 * 60 * 60) ->
                Pair("${intervalSec / (24 * 60 * 60)}", "天")
            intervalSec >= 60 * 60 ->
                Pair("${intervalSec / (60 * 60)}", "小时")
            else ->
                Pair("${intervalSec / 60}", "分钟")
        }
    }

    fun updateScoreInfo() {
        safeLaunch {
            //先使用数据库数据，再在后台更新成绩信息
            this@MainViewModel.scores.postValue(Repository.ScoreRt.getNow())
            Repository.ScoreRt.fetchNow().data?.run {
                this@MainViewModel.scores.postValue(this)
            }
        }
    }

    fun updateWeather() {
        safeLaunch {
            Repository.WeatherRt.fetch("101230101").data?.run {
                this@MainViewModel.weather.postValue(this)
            }
        }
    }

    fun importAccount() {
        GlobalScope.launch {
            try {
                val cm = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val data = cm.primaryClip
                val item = data!!.getItemAt(0)
                val content = item.text.toString()
                val list = JSONObject.parseArray(content, User::class.java)
                list.forEach {
                    Repository.user.save(it)
                }
                event.showMessage("导入成功")
            } catch (e: Exception) {
                event.showMessage("导入失败")
            }
        }
    }

    fun checkTheme() {
        safeLaunch {
            theme.postValue(Repository.GlobalSettingRt.get().theme)
        }
    }

    fun upgradeApp() {
        safeLaunch {
            val upgradeInfo = Beta.getUpgradeInfo()
            if (upgradeInfo != null && upgradeInfo.versionCode > GlobalLib.getLocalVersionCode(getApplication())) {
                Beta.checkUpgrade()
            } else {
                event.showMessage(R.string.is_last_version)
            }
        }
    }

    fun switchAccount() {
        safeLaunch {
            users.postValue(Repository.user.getAll())
            isShowSwitchAccountDialog.postValue(true)
        }
    }

    fun addAccountSuccess() {
        safeLaunch {
            val user = Repository.user.getInUse()
            initActivityData()
            event.showMessage("已切换到${user?.account}")
        }
    }

    fun deleteUser(user: User) {
        safeLaunch {
            if (user.account != inUseUser.value?.account) {
                Repository.user.delete(user.account)
                Repository.user.getInUse().run {
                    if (this@run == null) {
                        event.startLoginActivity()
                    } else {
                        event.showMessage("已切换到${account}")
                        isShowSwitchAccountDialog.postValue(false)
                        //重新初始化数据
                        initActivityData()
                    }
                }
            }
        }
    }

    fun checkoutTo(user: User) {
        safeLaunch {
            if (user.account != Repository.user.getInUseAccount()) {
                event.showDialog()
                Repository.user.saveLoginOnly(user)
                val job = safeLaunch {
                    Repository.user.login(user)
                }
                IFAFU.loginJob = job
                job.join()
                event.showMessage("成功切换到${user.account}")
                isShowSwitchAccountDialog.postValue(false)
                //重新初始化Activity
                initActivityData()
                event.hideDialog()
            }
        }
    }
}