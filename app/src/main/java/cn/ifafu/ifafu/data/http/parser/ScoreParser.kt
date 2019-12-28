package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.entity.User
import org.jsoup.Jsoup
import java.util.*

class ScoreParser(user: User) : BaseParser<List<Score>>() {
    private val account: String = user.account
    private val schoolCode: String = user.schoolCode
    override fun parse(html: String): List<Score> {
        val list: MutableList<Score> = ArrayList()
        val document = Jsoup.parse(html)
        val elementsTemp = document.select("table[id=\"Datagrid1\"]")
        if (elementsTemp.size == 0) {
            return emptyList()
        }
        val elements = elementsTemp[0].getElementsByTag("tr")
        when (schoolCode) {
            School.FAFU -> {
                var i = 1
                while (i < elements.size) {
                    list.add(paresToScoreFAFU(elements[i].children().eachText()))
                    i++
                }
            }
            School.FAFU_JS -> {
                var i = 1
                while (i < elements.size) {
                    list.add(paresToScoreFAFUJS(elements[i].children().eachText()))
                    i++
                }
            }
        }
        for (score in list) {
            score.account = account
            score.id = score.id * 31 + account.hashCode()
        }
        return list
    }

    private fun paresToScoreFAFU(eles: List<String>): Score {
        val score = Score()
        score.year = eles[0]
        score.term = eles[1]
        score.id = eles[2].toLong()
        score.name = eles[3]
        score.nature = eles[4]
        score.attr = eles[5]
        score.credit = eles[6].toFloatOrNull() ?: -1F
        val ele7 = eles[7]
        if (ele7.contains("免修")) {
            score.score = Score.FREE_COURSE
        } else {
            score.score = eles[7].toFloatOrNull() ?: -1F
        }
        score.makeupScore = eles[8].toFloatOrNull() ?: -1F
        score.restudy = eles[9].isEmpty()
        score.institute = eles[10]
        if (eles.size > 13) {
            score.gpa = eles[11].toFloatOrNull() ?: -1F
            score.remarks = eles[12]
            score.makeupRemarks = eles[13]
        } else {
            score.remarks = eles[11]
            score.makeupRemarks = eles[12]
        }
        return score
    }

    private fun paresToScoreFAFUJS(eles: List<String>): Score {
        val score = Score()
        score.id = eles[2].toLong()
        score.year = eles[0]
        score.term = eles[1]
        score.name = eles[3]
        score.nature = eles[4]
        score.attr = eles[5]
        score.credit = eles[6].toFloatOrNull() ?: -1F
        score.gpa = eles[7].toFloatOrNull() ?: -1F
        score.score = eles[8].toFloatOrNull() ?: -1F
        score.makeupScore = eles[10].toFloatOrNull() ?: -1F
        score.institute = eles[12]
        try {
            score.remarks = eles[13]
            score.makeupRemarks = eles[14]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return score
    }

}