package cn.ifafu.ifafu.data.retrofit.parser

import cn.ifafu.ifafu.data.bean.Response
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.User
import org.jsoup.Jsoup
import java.util.*

class ScoreParser(user: User) : BaseParser<Response<List<Score>>>() {
    private val account: String = user.account

    override fun parse(html: String): Response<List<Score>> {
        check(html)
        val list: MutableList<Score> = ArrayList()
        val document = Jsoup.parse(html)
        val elementsTemp = document.select("table[id=\"Datagrid1\"]")
        if (elementsTemp.size == 0) {
            return Response.failure("成绩获取失败")
        }
        val elements = elementsTemp[0].getElementsByTag("tr")
        elements.drop(1).forEach {
            //Element.eachText会去除空字符串
            val eachText = it.children().text().split(" ")
            list.add(paresToScore(eachText))
        }
        for (score in list) {
            score.account = account
            score.id = score.id * 31 + account.hashCode()
        }
        return Response.success(list.sortedBy { it.id })
    }

    private fun paresToScore(eles: List<String>): Score {
        val score = Score()
        score.year = eles[0]
        score.term = eles[1]
        score.id = (eles[0] + eles[1] + eles[2]).hashCode().toLong()
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

}