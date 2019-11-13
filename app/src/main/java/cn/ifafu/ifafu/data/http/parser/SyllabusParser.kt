package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.util.RegexUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by woolsen on 19/8/1
 */
class SyllabusParser(user: User?) : BaseParser<MutableList<Course>>() {

    private val weekdayCN = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    private val weekday = intArrayOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)

    //用于标记课程位置
    private val locFlag = Array(24) { BooleanArray(20) }

    private val account: String = user?.account ?: "null"

    override fun parse(html: String): MutableList<Course> {
        val courses = ArrayList<Course>()
        val doc = Jsoup.parse(html)
        val nodeTrs = doc.getElementById("Table1").getElementsByTag("tr")
        val help = Help()
        for (i in 2 until nodeTrs.size) { //定位到第几节课一行过的所有元素

            val tds = nodeTrs[i].getElementsByTag("td")
            //开始节数的备用方案，通过解析侧边节数获取
            var index = 0
            while (index < 2) {
                val s = tds[index].text()
                val nodeRegex = "第[0-9]{1,2}节"
                if (s.matches(nodeRegex.toRegex())) {
                    help.beginNode = RegexUtils.getNumbers(s)[0]
                    index++
                    break
                }
                index++
            }
            for (j in 0 until tds.size - index) {  //定位到第几节课第几格的元素
                val td = tds[index + j]
                //列的备用方案
                help.col = j
                var hNode = 0
                for (col in 1..7) {
                    if (locFlag[help.beginNode][col % 7]) {
                        hNode++
                    } else {
                        break
                    }
                }
                help.col = hNode
                //通过“rowspan“获取 课程节数(备用方案)
                if (td.hasAttr("rowspan")) {
                    help.rowspan = Integer.parseInt(td.attr("rowspan"))
                } else {
                    help.rowspan = 1
                }

//                println("help: $help")
//                println("td: $td")
//                println(help)
                mark(help) //标记课程位置
//                println()

                parseTdElement2(td, help)?.run {
                    courses.addAll(this)
                }
            }
        }
//        testHelpPrintf()
        return merge(courses).apply {
            forEach {
                it.account = account
                it.id = it.hashCode().toLong()
            }
        }
    }

    private fun parseTdElement2(td: Element, help: Help): List<Course>? {
        val text = td.text()
        if (text.isEmpty()) {
            return null
        }
        val list = ArrayList<Course>()

        for (s1 in td.html().split("(<br>){2,3}".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            println(s1)
//            if (s1.contains("\\([调停换]".toRegex())) {
//                continue
//            }
            kotlin.runCatching {
                //TODO 调课、换课
                val info = s1.split("<br>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val course = Course()
                course.name = info[0]
                parseTime(course, info[1], help)
                course.teacher = info[2]
                if (info.size > 3) {
                    course.address = info[3]
                }
                course.account = account
                list.add(course)
            }.onFailure {
                it.printStackTrace()
            }
        }
        return list
    }

    /**
     * flag标记课程位置，用于备用方案的列定位
     */
    private fun mark(help: Help) {
        for (i in 0 until help.rowspan) {
            locFlag[i + help.beginNode][help.col + 1] = true
        }
//        testHelpPrintf()
    }

    //测试用
    private fun testHelpPrintf() {
        print("   ")
        for (j in 1 until 8) {
            print(String.format("%2d  ", j))
        }
        println()
        for (i in 1..13) {
            var first = true
            for (j in 1 until locFlag[i].size) {
                if (first) {
                    print(String.format("%2d  ", i))
                    first = false
                }
                if (locFlag[i][j]) {
                    print("●   ")
                } else {
                    print("○   ")
                }
            }
            println()
        }
    }

    private fun parseTime(course: Course, text: String, help: Help) {
        //beginNode, rowspan
        val m1 = Pattern.compile("第.*节").matcher(text)
        if (m1.find() && !m1.group().contains("-")) {
            val intList = RegexUtils.getNumbers(m1.group())
            course.beginNode = intList[0]
            course.nodeCnt = intList.size
        } else {
            course.beginNode = help.beginNode
            val m2 = Pattern.compile("[0-9]+节\\\\周").matcher(text)
            if (m2.find()) {
                course.nodeCnt = RegexUtils.getNumbers(m2.group())[0]
            } else {
                course.nodeCnt = help.rowspan
            }
        }

        //weekdayCN
        var flag = false
        for (i in 0..6) {
            if (text.contains(weekdayCN[i])) {
                course.weekday = weekday[i]
                flag = true
                break
            }
        }
        if (!flag) {
            course.weekday = when (help.col) {
                0 -> Calendar.MONDAY
                1 -> Calendar.TUESDAY
                2 -> Calendar.WEDNESDAY
                3 -> Calendar.THURSDAY
                4 -> Calendar.FRIDAY
                5 -> Calendar.SATURDAY
                6 -> Calendar.SUNDAY
                else -> 0
            }
        }

        val m2 = Pattern.compile("第[0-9]+-[0-9]+周").matcher(text)
        if (m2.find()) {
            val intList = RegexUtils.getNumbers(m2.group())
            var beginWeek = intList[0]
            val endWeek = intList[1]
            //weekType
            if (text.contains("单周")) {
                beginWeek = if (beginWeek % 2 == 1) beginWeek else beginWeek + 1
                var i = beginWeek
                while (i <= endWeek) {
                    course.weekSet.add(i)
                    i += 2
                }
            } else if (text.contains("双周")) {
                beginWeek = if (beginWeek % 2 == 0) beginWeek else beginWeek + 1
                var i = beginWeek
                while (i <= endWeek) {
                    course.weekSet.add(i)
                    i += 2
                }
            } else {
                for (i in beginWeek..endWeek) {
                    course.weekSet.add(i)
                }
            }
        }
    }

    private fun merge(courses: List<Course>): MutableList<Course> {

        val map = HashMap<String, Array<BooleanArray>>()
        courses.forEach { course ->
            val key = "${course.name}❤${course.teacher ?: ""}❤${course.address ?: ""}❤${course.weekday}"
            val nodes = map.getOrPut(key, { Array(25) { BooleanArray(20) } })
            course.weekSet.forEach { week ->
                for (node in course.beginNode until (course.beginNode + course.nodeCnt)) {
                    nodes[week][node] = true
                }
            }
        }

        val afterMergeCourses = ArrayList<Course>()

        for ((k, nodes) in map) {
//            println(k)
//            for (i in 1..24) {
//                print("%2d ".format(i))
//            }
//            println()
//            for (i in 1 until 13) {
//                for (j in 1 until 25) {
//                    if (nodes[j][i]) {
//                        print(" ● ")
//                    } else {
//                        print(" ○ ")
//                    }
//                }
//                println()
//            }
            val info = k.split("❤")
            for (week in 0 until 24) {
                for (node in 0 until 13) {
                    if (nodes[week][node]) {

                        var nodeLength = 1
                        while (nodes[week][node + nodeLength]) {
                            nodeLength++
                        }

                        val course = Course()
                        course.name = info[0]
                        course.teacher = info[1]
                        course.address = info[2]
                        course.weekday = info[3].toInt()
                        course.beginNode = node
                        course.nodeCnt = nodeLength

                        var weekDump = 0
                        lengthWhile@ while (week + weekDump <= 24) {
                            var flag = true
                            for (j1 in 0 until nodeLength) {
                                if (!nodes[week + weekDump][node + j1]) {
                                    flag = false
                                    break
                                }
                            }
                            if (flag) {
                                course.weekSet.add(week + weekDump)
                                for (j1 in 0 until nodeLength) {
                                    nodes[week + weekDump][node + j1] = false
                                }
                            }
                            weekDump++
                        }
                        afterMergeCourses.add(course)
                    }
                }
            }
        }
        afterMergeCourses.sortBy { it.name + it.weekday }

        return afterMergeCourses
    }

    private data class Help(
            var beginNode: Int = 0,
            var rowspan: Int = 0,
            var col: Int = 0
    )

}
