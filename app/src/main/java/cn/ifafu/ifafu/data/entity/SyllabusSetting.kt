package cn.ifafu.ifafu.data.entity

import android.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Entity
class SyllabusSetting {
    @PrimaryKey
    var account: String = ""
    var weekCnt = 20 //总共周数
    var totalNode = 12 //每日课程节数
    var showSaturday = true //显示周六
    var showSunday = true //显示周日
    var showBeginTimeText = true //显示侧边栏时间
    var showHorizontalLine = true //显示水平分割线
    var showVerticalLine = true //显示竖直分割线
    var openingDay = "2020-03-15" //开学时间
    var nodeLength = 45 //一节课的时间
    var firstDayOfWeek = Calendar.SUNDAY //每周的第一天
    var background //课表背景
            : String = ""
    var textSize = 12 //课程字体大小
    var themeColor = Color.BLACK //主题颜色
    var statusDartFont = true //状态栏深色字体
    var isForceRefresh = false // 每次进入课表，自动刷新课表
    var parseType = 1 //1:本地解析   2：网络解析

    var beginTime: List<Int> = ArrayList()

    @Ignore
    constructor(account: String) {
        this.account = account
    }

    constructor()

    val beginTimeText: Array<String>
        get() {
            return beginTime.map {
                String.format("%d:%02d", it / 100, it % 100)
            }.toTypedArray()
        }

    /**
     * @return 小于0则为放假中
     */
    fun getCurrentWeek(): Int {
        return try {
            val calendar = Calendar.getInstance()
            val now = calendar.timeInMillis
            val openingDay = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(openingDay)!!.time
            val calcWeek = ((now - openingDay) / 1000 / 60 / 60 / 24 / 7 + 1).toInt()
            if (calcWeek > weekCnt) {
                -1
            } else {
                calcWeek
            }
        } catch (e: Exception) {
            -1
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as SyllabusSetting
        return weekCnt == that.weekCnt &&
                totalNode == that.totalNode &&
                showSaturday == that.showSaturday &&
                showSunday == that.showSunday &&
                showBeginTimeText == that.showBeginTimeText &&
                showHorizontalLine == that.showHorizontalLine &&
                showVerticalLine == that.showVerticalLine &&
                nodeLength == that.nodeLength &&
                firstDayOfWeek == that.firstDayOfWeek &&
                textSize == that.textSize &&
                themeColor == that.themeColor &&
                statusDartFont == that.statusDartFont &&
                isForceRefresh == that.isForceRefresh &&
                account == that.account &&
                openingDay == that.openingDay &&
                background == that.background &&
                beginTime == that.beginTime
    }

    override fun hashCode(): Int {
        return Objects.hash(account, weekCnt, totalNode, showSaturday, showSunday, showBeginTimeText, showHorizontalLine, showVerticalLine, openingDay, nodeLength, firstDayOfWeek, background, textSize, themeColor, statusDartFont, isForceRefresh, beginTime)
    }

    companion object {
        @JvmField
        var intBeginTime = arrayOf(
                arrayOf(0, 800, 850, 955, 1045, 1135, 1400, 1450, 1550, 1640, 1825, 1915, 2005),
                arrayOf(0, 830, 920, 1025, 1115, 1205, 1400, 1450, 1545, 1635, 1825, 1915, 2005)
        )
    }
}