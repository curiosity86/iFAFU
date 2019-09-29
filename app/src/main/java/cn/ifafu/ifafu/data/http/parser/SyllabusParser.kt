package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.util.RegexUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import java.util.regex.Pattern

/**
 * Created by woolsen on 19/8/1
 */
class SyllabusParser(user: User?) : BaseParser<List<Course>>() {

    private val weekdayCN = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    private val weekday = intArrayOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)

    //用于标记课程位置
    private val locFlag = Array(20) { BooleanArray(8) }

    private val account: String = user?.account ?: "null"

    override fun parse(html: String): List<Course> {
        val courses = ArrayList<Course>()
        val doc = Jsoup.parse(html)
        val nodeTrs = doc.getElementById("Table1").getElementsByTag("tr")
        val help = Help()
        for (i in 2 until nodeTrs.size) {
            //定位到课程元素
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
            //解析课程信息
            for (j in 0 until tds.size - index) {
                val td = tds[index + j]
                //列的备用方案
                help.col = j
                var hNode = 0
//                if (tds[j].text().contains("测试")) {
//                    println("-------断点-------")
//                    testHelpPrintf()
//                    println("help: $help")
//                }
                for (col in 1..7) {
                    if (locFlag[help.beginNode][col % 7]) {
                        hNode ++
                    } else {
                        break
                    }
                }
                help.col += hNode
                //课程节数的备用方案，通过“rowspan“获取
                if (td.hasAttr("rowspan")) {
                    help.nodeNum = Integer.parseInt(td.attr("rowspan"))
                }
                val clist = parseTdElement2(td, help)
                if (clist != null) {
                    courses.addAll(clist)
                    for (c in clist) {
                        mark(c)
                    }
                }
            }
        }
//        println("---------END----------")
//        testHelpPrintf()
        return merge(courses)
    }

    private fun parseTdElement2(td: Element, help: Help): List<Course>? {
        val text = td.text()
        if (text.isEmpty()) {
            return null
        }
        val list = ArrayList<Course>()

        for (s1 in td.html().split("<br><br>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            if (!s1.contains("(调") && !s1.contains("(换")) {
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
                course.id = course.hashCode().toLong()
                list.add(course)
            }
        }
        return list
    }

    /**
     * flag标记课程位置，用于定位
     */
    private fun mark(course: Course) {
        var col = course.weekday - 1
        if (col == 0) {
            col = 7
        }
        for (i in 0 until course.nodeCnt) {
            locFlag[i + course.beginNode][col] = true
        }
//        println("${course.name}, beginNode=${course.beginNode}, nodeCnt=${course.nodeCnt}, weekday=${course.weekday}")
//        testHelpPrintf()
    }

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
//        if (course.name.contains("测试")) {
//            println("------测试------")
//            println("course: $course")
//            println("text: $text")
//            println("help: $help")
//            testHelpPrintf()
//        }
        //beginNode, nodeNum
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
                course.nodeCnt = help.nodeNum
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

    private fun merge(courses: List<Course>): List<Course> {
        val newList = ArrayList<Course>()
        val map = HashMap<String, MutableList<Course>>()
        for (course in courses) {
            val key = (course.name + course.teacher + course.address + course.weekday
                    + course.weekSet.first() + course.weekSet.last())
            if (map[key] == null) {
                map[key] = ArrayList()
            }
            map[key]!!.add(course)
        }
        for (cl in map.values) {
            cl.sortWith(Comparator { o1, o2 -> o1.beginNode.compareTo(o2.beginNode) })
            if (cl.size == 2 && cl[0].beginNode + cl[0].nodeCnt == cl[1].beginNode) {
                cl[0].nodeCnt = cl[0].nodeCnt + cl[1].nodeCnt
                cl.removeAt(1)
            }
            newList.addAll(cl)
        }
        return newList
    }

    private data class Help(
        internal var beginNode: Int = 0,
        internal var nodeNum: Int = 0,
        internal var col: Int = 0
    )

}
