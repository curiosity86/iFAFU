package cn.ifafu.ifafu.data.retrofit.parser

import cn.ifafu.ifafu.data.exception.NoLogException
import org.jsoup.Jsoup
import java.util.*
import java.util.regex.Pattern

open class ParamsParser : BaseParser<MutableMap<String, String>>() {

    @Throws(Exception::class)
    override fun parse(html: String): MutableMap<String, String> {
//        println(html)
        val p = Pattern.compile("alert\\('.*'\\);")
        val m = p.matcher(html)
        if (m.find()) {
            val s = m.group()
            if (s.matches("现在不能查询".toRegex())) {
                throw NoLogException(s.substring(7, s.length - 3))
            }
        }
        val params = HashMap<String, String>()
        val document = Jsoup.parse(html)
        val elements = document.select("input[type=\"hidden\"]")
        for (element in elements) {
            params[element.attr("name")] = element.attr("value")
        }
        return params
    }

}
