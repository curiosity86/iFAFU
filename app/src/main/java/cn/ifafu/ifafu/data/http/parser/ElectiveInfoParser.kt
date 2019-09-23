package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.data.entity.ElectivesInfo
import cn.ifafu.ifafu.data.entity.User
import org.jsoup.Jsoup

class ElectiveInfoParser(val user: User) : BaseParser<ElectivesInfo>() {

    @Throws(Exception::class)
    override fun parse(html: String): ElectivesInfo {
        val info = ElectivesInfo()
        info.account = user.account
        val doc = Jsoup.parse(html)
        when (user.schoolCode) {
            School.FAFU -> {
                runCatching {
                    val ele = doc.select("table[id=\"DataGrid5\"]")[0].children()[0].children()
                    for (element in ele) {
                        val ts = element.text().trim().split(" ")
                        if (ts.size < 2) continue
                        when (ts[0]) {
                            "自然科学类" -> info.zrkx = ts[1].toIntOrNull() ?: 0
                            "人文社科类" -> info.rwsk = ts[1].toIntOrNull() ?: 0
                            "艺术、体育类" -> info.ysty = ts[1].toIntOrNull() ?: 0
                            "文学素养类" -> info.wxsy = ts[1].toIntOrNull() ?: 0
                            "创新创业教育类" -> info.cxcy = ts[1].toIntOrNull() ?: 0
                        }
                    }
                }.onFailure {
                    info.set(0, 0, 0, 0, 0)
                }
            }
            School.FAFU_JS -> {
                val ele = doc.select("option[selected=\"selected\"]")
                when (ele[1].text()) {
                    "经济与管理系", "人文社会科学系" ->
                        info.set(4, 0, 2, 0, 0)
                    "信息与机电工程系", "工程技术系", "农业与生物技术系" ->
                        info.set(0, 4, 2, 0, 0)
                    "文学艺术系" -> {
                        when (ele[2].text()) {
                            "产品设计", "环境设计", "视觉传达设计" ->
                                info.set(4, 0, 0, 0, 0)
                            else ->
                                info.set(4, 0, 2, 0, 0)
                        }
                    }
                    else ->
                        info.set(2, 2, 2, 2, 2)
                }
            }
            else ->
                info.set(2, 2, 2, 2, 2)
        }
        runCatching {
            val ttt = doc.select("table[id=\"DataGrid4\"]").text().split(" ")
            info.total = ttt[ttt.indexOf("任意选修课") + 1].toInt()
        }
        return info
    }
}
