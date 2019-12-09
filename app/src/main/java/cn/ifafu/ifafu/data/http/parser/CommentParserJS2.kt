package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.util.encode
import org.jsoup.Jsoup
import java.util.*

class CommentParserJS2 : BaseParser<MutableMap<String, String>>() {

    @Throws(Exception::class)
    override fun parse(html: String): MutableMap<String, String> {
        val map = HashMap<String, String>()
        val document = Jsoup.parse(html)
        val form = document.selectFirst("form[id=Form1]")
        val table = form.selectFirst("table[id=\"DataGrid1\"]")
        val trs = table.children()[0].children()
        for (i in 1 until trs.size) {
            val tr = trs[i].children()
            for (input in tr.select("input")) {
                map[input.attr("name")] = input.`val`()
            }
            for (j in 3 until tr.size) {
                val tdChild = tr[j].children()
                //<select>
                val select = tdChild[0]
                val selectedOption = select.selectFirst("option[selected=selected]")
                if (selectedOption != null) {
                    map[select.attr("name")] = selectedOption.`val`()
                } else {
                    map[select.attr("name")] = "优秀"
                }
            }
        }
        //保证至少有一项评分为良好
        val lastTr = trs.last().children()
        for (i in 3 until lastTr.size) {
            val tdChild = lastTr[i].children()
            //<select>
            val select = tdChild[0]
            map[select.attr("name")] = "良好"
        }
        //添加评价课程名称Params
        val select = form.selectFirst("select[name=pjkc]")
        map[select.attr("name")] = select.selectFirst("option[selected=selected]").`val`()
        //添加其余Params <input>
        val inputs = form.getElementsByTag("input")
        val hiddenInputs = inputs.select("input[type=hidden]")
        for (input in hiddenInputs) {
            map[input.attr("name")] = input.`val`()
        }
        map["Button1"] = "保  存"
        map["pjxx"] = ""
        map["TextBox1"] = "0"
        map["txt1"] = ""
        map.keys.forEach {
            map[it] = map[it]!!.encode("gb2312")
        }
        return map
    }
}
