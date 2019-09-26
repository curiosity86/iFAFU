package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.Response
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.util.RegexUtils
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.util.*
import kotlin.collections.ArrayList

class ExamParser(user: User) : BaseParser<Response<List<Exam>>>() {

    private val account: String = user.account
    private val schoolCode: Int = user.schoolCode

    override fun parse(html: String): Response<List<Exam>> {
        val document = Jsoup.parse(html)
        val elementsTemp = document.select("table[id=\"DataGrid1\"]")
        if (elementsTemp.size == 0) {
            return Response.success(emptyList())
        }
        val elements = elementsTemp[0].getElementsByTag("tr")
        val list = ArrayList<Exam>()
        val termAndYear = document.select("option[selected=\"selected\"]")
        val year = termAndYear[0].text()
        val term = termAndYear[1].text()
        for (i in 1 until elements.size) {
            val exam = getExam(elements[i].children())
            exam.term = term
            exam.year = year
            list.add(exam)
        }
        return Response.success(list)
    }

    private fun getExam(e: Elements): Exam {
        val numbers = RegexUtils.getNumbers(e[3].text())
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        calendar.set(Calendar.YEAR, numbers[0])
        calendar.set(Calendar.MONTH, numbers[1] - 1)
        calendar.set(Calendar.DAY_OF_MONTH, numbers[2])
        calendar.set(Calendar.HOUR_OF_DAY, numbers[3])
        calendar.set(Calendar.MINUTE, numbers[4])
        val start = calendar.time.time

        calendar.set(Calendar.HOUR_OF_DAY, numbers[5])
        calendar.set(Calendar.MINUTE, numbers[6])
        val end = calendar.time.time

        val exam = Exam()
        exam.id = e[0].text().hashCode().toLong()
        exam.name = e[1].text()
        exam.address = e[4].text()
        exam.startTime = start
        exam.endTime = end
        exam.seatNumber = e[6].text().run {
            if (this.matches("[0-9]+".toRegex())) this + "号" else this
        }
        exam.account = account

        return exam
    }

}
