package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.entity.exception.NoAuthException
import cn.ifafu.ifafu.entity.exception.NoLogException
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern

class ParamsParser2 {

    @Throws(Exception::class)
    fun parse(response: Response<ResponseBody>): MutableMap<String, String> {
        if (response.code() == 302) {
            throw NoAuthException()
        }
        val html = response.body()!!.string()
//        println("code: " + response.code())
//        println("message: " + response.message())
//        println("string: " + html)
        val p = Pattern.compile("alert\\('.*'\\);")
        val m = p.matcher(response.body()!!.string())
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
