package cn.ifafu.ifafu.data.http.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.HashMap
import java.util.Random

class CommentParser2 : ParamsParser() {

    @Throws(Exception::class)
    override fun parse(html: String): MutableMap<String, String> {
        val document = Jsoup.parse(html)
        val table = document.select("table[id=\"Datagrid1\"]") //定位到表格
        val lines = table[0].getElementsByTag("table")
        val map = HashMap<String, String>()
        for (i in 1 until lines.size) {
            val e = lines[i]
            map[e.attr("id").replace("__", ":_").replace("_rb", ":rb")] = "94"
        }
        map[lines.last().attr("id").replace("__", ":_").replace("_rb", ":rb")] = "82"
        map.putAll(super.parse(html))
        return map
    }
}
