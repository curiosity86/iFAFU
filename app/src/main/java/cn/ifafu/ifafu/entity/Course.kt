package cn.ifafu.ifafu.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.view.syllabus.CourseBase
import cn.ifafu.ifafu.view.syllabus.ToCourseBase
import java.util.*

@Entity
class Course : ToCourseBase {

    @PrimaryKey
    var id: Long = 0

    var name // 课程名
            : String = ""
    var address // 上课地点
            : String = ""
    var teacher // 老师名
            : String = ""
    var weekday = 0 // 星期几 = 0
    var beginNode = 0 // 开始节数 = 0
    var nodeCnt = 0 // 上课节数

    var weekSet = TreeSet<Int>() //第几周需要上课
    var color = 0 // 课程颜色
    var account // 课程归属账号
            : String = ""
    var local = false // 是否是自定义课程

    val endNode: Int
        get() = beginNode + nodeCnt - 1

    override fun toCourseBase(): CourseBase {
        val courseBase = CourseBase()
        if (address.isEmpty()) {
            courseBase.text = name
        } else {
            courseBase.text = "$name\n@$address"
        }
        courseBase.beginNode = beginNode
        courseBase.weekday = weekday
        courseBase.nodeCnt = nodeCnt
        courseBase.setOther(this)
        return courseBase
    }

    override fun toString(): String {
        return "Course{" + name +
                ", " + address +
                ", " + teacher +
                ", " + DateUtils.getWeekdayCN(weekday) +
                "第" + beginNode + "-" + (beginNode + nodeCnt - 1) + "节" +
                ", " + weekSet +
                '}'
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val course = other as Course
        return weekday == course.weekday && beginNode == course.beginNode && nodeCnt == course.nodeCnt && local == course.local &&
                name == course.name &&
                address == course.address &&
                teacher == course.teacher &&
                weekSet == course.weekSet &&
                account == course.account
    }

    override fun hashCode(): Int {
        return Objects.hash(name, address, teacher, weekday, beginNode, nodeCnt, weekSet, account, local)
    }
}