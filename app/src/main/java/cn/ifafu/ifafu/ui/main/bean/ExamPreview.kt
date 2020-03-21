package cn.ifafu.ifafu.ui.main.bean

import cn.ifafu.ifafu.data.entity.Exam
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExamPreview(
        val hasInfo: Boolean,
        val message: String = "",
        val items: Array<Item?> = arrayOfNulls(2)
) {

    class Item(
            val examName: String,
            val examTime: String,
            val address: String,
            val seatNumber: String,
            val timeLeftAndUnit: Array<String>
    )

    companion object {
        fun convert(exams: List<Exam>): ExamPreview {
            val list = ArrayList<Item>()
            val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.CHINA)
            val now = System.currentTimeMillis()
            val maxSize = if (exams.size < 2) exams.size else 2
            for (i in 0 until maxSize) {
                var time: String
                var last: Array<String>
                if (exams[i].startTime == 0L) {
                    time = "暂无考试时间"
                    last = arrayOf("", "")
                } else {
                    time = dateFormat.format(Date(exams[i].startTime)) +
                            "(${timeFormat.format(Date(exams[i].startTime))}" +
                            "-" +
                            "${timeFormat.format(Date(exams[i].endTime))})"
                    last = calcIntervalTimeForNextExam(now, exams[i].startTime)
                }
                list.add(Item(
                        examName = exams[i].name,
                        examTime = time,
                        address = exams[i].address,
                        seatNumber = exams[i].seatNumber,
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
}
