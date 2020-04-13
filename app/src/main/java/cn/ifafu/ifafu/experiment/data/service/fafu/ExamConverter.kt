package cn.ifafu.ifafu.experiment.data.service.fafu

import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.util.getInts
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import timber.log.Timber
import java.util.*

class ExamConverter {

    fun convert(html: String): IFResponse<List<Exam>> {
        try {
            val document = Jsoup.parse(html)
            val elementsTemp = document.select("table[id=\"DataGrid1\"]")
            if (elementsTemp.size == 0) {
                return IFResponse.Success(emptyList())
            }
            val termAndYear = document.select("option[selected=\"selected\"]")
            val year = termAndYear[0].text()
            val term = termAndYear[1].text()
            val list = elementsTemp[0].getElementsByTag("tr")
                    .drop(1)
                    .map { it.children() }
                    .map { getExam(it) }
                    .onEach {
                        it.term = term
                        it.year = year
                    }
            Timber.d("ExamConverter#convert => ${list.map { it.name }.joinToString(", ")}")
            return IFResponse.Success(list)
        } catch (e: Exception) {
            return IFResponse.Failure("获取考试失败")
        }
    }

    private fun getExam(e: Elements): Exam {
        val exam = Exam()
        val numbers = e[3].text().getInts()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        //考试时间
        if (numbers.size > 6) {
            calendar.set(Calendar.YEAR, numbers[0])
            calendar.set(Calendar.MONTH, numbers[1] - 1)
            calendar.set(Calendar.DAY_OF_MONTH, numbers[2])
            val start = calendar.apply {
                set(Calendar.HOUR_OF_DAY, numbers[3])
                set(Calendar.MINUTE, numbers[4])
            }.time.time

            val end = calendar.apply {
                set(Calendar.HOUR_OF_DAY, numbers[5])
                set(Calendar.MINUTE, numbers[6])
            }.time.time
            exam.startTime = start
            exam.endTime = end
        }
        //考试名、地址、座位号
        exam.name = e[1].text()
        exam.address = if (e.size > 4) e[4].text() else ""
        exam.seatNumber = if (e.size > 6) e[6].text().run {
            if (this.matches("[0-9]+".toRegex())) this + "号"
            else this
        } else ""
        return exam
    }

}
