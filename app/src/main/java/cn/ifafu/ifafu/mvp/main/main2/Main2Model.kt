package cn.ifafu.ifafu.mvp.main.main2

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.mvp.main.BaseMainModel
import cn.ifafu.ifafu.mvp.syllabus.SyllabusModel
import cn.ifafu.ifafu.util.DateUtils
import io.reactivex.Observable
import java.text.MessageFormat
import java.util.*

class Main2Model(context: Context) : BaseMainModel(context), Main2Contract.Model {
    private lateinit var toYear: String
    private lateinit var toTerm: String

    override fun getAllUser(): MutableList<User> {
        return repository.allUser
    }

    override fun saveLoginUser(user: User) {
        repository.saveLoginUser(user)
    }

    override fun getLoginUser(): User? {
        var user = repository.loginUser
        if (user == null) {
            user = repository.allUser.getOrNull(0)
            if (user != null) {
                saveLoginUser(user)
            }
            return user
        }
        return user
    }

    override fun deleteAccount(user: User) {
        repository.deleteUser(user)
    }

    override fun getSyllabusSetting(): SyllabusSetting {
        return repository.syllabusSetting
    }

    override fun getFunctionTab(): Map<String, List<Pair<String, Int>>> {
        return mapOf(
                "信息查询" to listOf(
                        Pair("成绩查询", R.drawable.tab_white_100),
                        Pair("学生考试查询", R.drawable.tab_white_exam),
                        Pair("电费查询", R.drawable.tab_white_elec)),
                "实用工具" to listOf(
                        Pair("我的课表", R.drawable.tab_white_syllabus),
                        Pair("网页模式", R.drawable.tab_white_web),
                        Pair("后勤服务", R.drawable.tab_white_repair)),
                "软件设置" to listOf(
                        Pair("软件设置", R.drawable.ic_setting3),
                        Pair("账号管理", R.drawable.tab_white_manage)),
                "关于软件" to listOf(
                        Pair("检查更新", R.drawable.tab_white_update),
                        Pair("关于iFAFU", R.drawable.tab_white_about)))
    }


    @SuppressLint("DefaultLocale")
    override fun getYearTermList(): Observable<YearTerm> {
        return Observable.fromCallable { repository.yearTerm }
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTerm(): Pair<String, String> {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 6)
        HashMap<String, String>()
        toTerm = if (c.get(Calendar.MONTH) < 8) "1" else "2"
        val year = c.get(Calendar.YEAR)
        toYear = String.format("%d-%d", year - 1, year)
        return Pair(toYear, toTerm)
    }

    override fun getNextCourse2(courses: List<Course>): NextCourse2 {
        val syllabusModel = SyllabusModel(mContext)
        val result = NextCourse2()
        val setting = syllabusModel.syllabusSetting
        var currentWeek = syllabusModel.currentWeek
        if (currentWeek <= 0 || currentWeek > setting.weekCnt) {
            result.title = "放假了呀！！"
            result.result = NextCourse.IN_HOLIDAY
            return result
        }
        result.weekText = MessageFormat.format("第{0}周", currentWeek)
        if (courses.isEmpty()) {
            result.title = "暂无课程信息"
            result.result = NextCourse.EMPTY_DATA
            return result
        }
        var currentWeekday: Int = DateUtils.getCurrentWeekday()
        //计算节假日
        syllabusModel.holidayFromToMap[currentWeek]?.run {
            this[currentWeekday]?.run {
                currentWeek = this.first
                currentWeekday = this.second
            }
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

//        //计算下一节是第几节课
        val intTime: IntArray = setting.beginTime
//        val c: Calendar = Calendar.getInstance()
//        val now = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE)
//        var nextNode = 9999
//        for (i in intTime.indices) {
//            if (now < intTime[i]) {
//                nextNode = i
//                break
//            }
//        }
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
                result.result = NextCourse.HAS_NEXT_COURSE
                result.address = course.address
                result.node = node
                result.timeText = String.format(Locale.CHINA, "%d:%02d-%d:%02d",
                        intStartTime / 100, intStartTime % 100, intEndTime / 100, intEndTime % 100)
                if (now >= intStartTime) {
                    //上课中
                    result.title = "当前：${course.name}"
                    result.result = NextCourse2.IN_COURSE
                    val last = (intEndTime / 100 - now / 100) * 60 + (intEndTime % 100 - now % 100)
                    result.lastText = (if (last >= 60) "${last / 60}小时" else "") +
                            (if (last % 60 != 0) "${last % 60}分钟" else "") +
                            "后下课"
                    break
                } else {
                    //即将上课
                    result.title = "下一节课：${course.name}"
                    result.result = NextCourse.HAS_NEXT_COURSE
                    val last = (intStartTime / 100 - now / 100) * 60 + (intStartTime % 100 - now % 100)
                    result.lastText = (if (last >= 60) "${last / 60 }小时" else "") +
                            (if (last % 60 != 0) "${last % 60}分钟" else "") +
                            "后上课"
                    break
                }
            }
        }
        if (result.title.isEmpty()) {
            result.title = "今天${result.totalNode}节课都上完了"
            result.result = NextCourse.NO_NEXT_COURSE
        }
        return result
    }

}