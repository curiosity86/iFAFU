package cn.ifafu.ifafu.data.newly.converter

import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

class ParamsConverter {

    fun convert(response: Response): Map<String, String> {
        val html = response.body()?.string()
                ?: throw IOException("response body can't be null")
        val pattern = Pattern.compile("alert\\('.*'\\);")
        val matcher = pattern.matcher(html)
        if (matcher.find()) {
            val text = matcher.group()
            if (text.matches("现在不能查询".toRegex())) {
                //返回弹窗信息
                throw IllegalAccessException(text.substring(7, text.length - 3))
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