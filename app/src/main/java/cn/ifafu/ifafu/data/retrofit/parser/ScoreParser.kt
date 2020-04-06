package cn.ifafu.ifafu.data.retrofit.parser

import cn.ifafu.ifafu.data.bean.Response
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.User
import org.jsoup.Jsoup

class ScoreParser(user: User) : BaseParser<Response<List<Score>>>() {
    private val account: String = user.account

    override fun parse(html: String): Response<List<Score>> {
        check(html)
        try {
            val list = Jsoup.parse(html)
                    .select("table[id=\"Datagrid1\"]")
                    .getOrElse(0) { return Response.failure("成绩获取失败") }
                    .getElementsByTag("tr")
                    .drop(1)
                    // 通过空格分隔信息，不能用Element#eachText（会把空字符串去掉）
                    .map { it.children().text().split(" ") }
                    .map { paresToScore(it, account) }
            return Response.success(list.sortedBy { it.id })
        } catch (e: Exception) {
            return Response.failure("成绩解析出错")
        }
    }

    private fun paresToScore(eles: List<String>, account: String): Score {
        val score = Score()
        score.account = account
        score.year = eles[0]
        score.term = eles[1]
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
        score.isIESItem = score.score == Score.FREE_COURSE
                || score.nature.contains("任意选修")
                || score.nature.contains("公共选修")
                || score.name.contains("体育")
        score.id = score.hashCode()
        return score
    }

}