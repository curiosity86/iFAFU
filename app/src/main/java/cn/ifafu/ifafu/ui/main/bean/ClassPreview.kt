package cn.ifafu.ifafu.ui.main.bean

import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ClassPreview private constructor(
        val hasInfo: Boolean = false, //是否有下一节课信息
        val message: String = "", //eg:放假中
        val nextClass: String = "", //课程名称  eg:当前\下一节课：${name}
        val address: String = "", //上课地点
        val numberOfClasses: Array<Int> = arrayOf(0, 0),  //[0]:当前上课节数， [1]:上课总节数
        val isInClass: Boolean = false, //上课中\未上课
        val classTime: String = "", //上课时间段
        val dateText: String,
        val timeLeft: String = "" //剩余上\下课时间
) {
    companion object {
        fun convert(courses: List<Course>, adjustInfo: Map<Int, Map<Int, Pair<Int, Int>?>>, setting: SyllabusSetting): ClassPreview {
            var currentWeek = setting.getCurrentWeek()
            var currentWeekday: Int = DateUtils.getCurrentWeekday()
            //计算节假日
            if (adjustInfo[currentWeek]?.containsKey(currentWeekday) == true) {
                currentWeek = -1
            } else {
                for1@ for ((week, pair) in adjustInfo) {
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
                        nextClass = courseInfo.name,
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
    }
}
