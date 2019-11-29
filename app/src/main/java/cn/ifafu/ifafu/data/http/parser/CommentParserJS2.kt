package cn.ifafu.ifafu.data.http.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.HashMap
import java.util.Random

class CommentParserJS2 : ParamsParser() {

    @Throws(Exception::class)
    override fun parse(html: String): MutableMap<String, String> {
        val map = HashMap<String, String>()
        val document = Jsoup.parse(html)
        val table = document.select("table[id=\"DataGrid1\"]") //定位到表格
        val select = document.select("select[name=\"pjkc\"]")
        map["pjkc"] = select[0].select("option[selected=\"selected\"]").attr("value")
        map["txt1"] = ""
        map["TextBox1"] = ""
        map["Button1"] = ""
        val lines = table[0].getElementsByTag("select")
        for (i in 0 until lines.size - 1) {
            val e = lines[i]
            val key = e.attr("name")
            map[key] = "优秀"
            map[key.replace("JS1", "txtjs1")] = ""
        }
        val last = lines.last().attr("name")
        map[last] = "良好"
        map[last.replace("JS1", "txtjs1")] = ""
        map.putAll(super.parse(html))
        return map
    }
}
