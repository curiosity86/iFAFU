package cn.ifafu.ifafu.data.new_http.converter

import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.exception.NoAuthException
import cn.ifafu.ifafu.util.getInts
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ExamConverter(user: User) {

    private val account: String = user.account
    private val schoolCode: String = user.school

    @Throws(NoAuthException::class)
    fun convert(response: Response): IFResult<List<Exam>> {
        val html = response.body()?.string()
                ?: throw IOException("response body can't be null")
        if (html.contains("请登录|请重新登陆|302 Found".toRegex())) {
            throw NoAuthException()
        }
        val document = Jsoup.parse(html)
        val elementsTemp = document.select("table[id=\"DataGrid1\"]")
        if (elementsTemp.size == 0) {
            return IFResult.success(ArrayList())
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
        return IFResult.success(list)
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
        exam.id = e[0].text().hashCode().toLong()
        exam.name = e[1].text()
        exam.address = if (e.size > 4) e[4].text() else ""
        exam.seatNumber = if (e.size > 6) e[6].text().run {
            if (this.matches("[0-9]+".toRegex())) this + "号"
            else this
        } else ""
        exam.account = account

        return exam
    }

}
