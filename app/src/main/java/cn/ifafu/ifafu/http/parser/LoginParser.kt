package cn.ifafu.ifafu.http.parser

import cn.ifafu.ifafu.data.Response
import cn.ifafu.ifafu.http.exception.VerifyErrorException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import java.util.regex.Pattern

class LoginParser : ObservableTransformer<ResponseBody, Response<String>> {

    /**
     * 登录页面解析
     *
     * @param html 网页信息
     * @return [Response.SUCCESS] 登录成功 body = user only with name
     * @return [Response.FAILURE] 信息错误 msg = return msg
     * @return [Response.ERROR]   服务器错误  msg = error msg
     */
    private fun parse(html: String): Response<String> {
        val doc = Jsoup.parse(html)
        val ele = doc.getElementById("xhxm")
        if (ele != null) {
            val name = ele.text().replace("同学", "")
            return Response.success(name)
        }
        val script = doc.select("script[language=javascript]")
        return if (script.size < 2) {
            Response.error("未知错误")
        } else {
            val s = getAlertString(script[1].html())
            if (s.contains("用户名") || s.contains("密码")) {
                Response.failure(s)
            } else if (doc.text().contains("ERROR")) {
                Response.error("教务系统又双叒崩溃了")
            } else if (s.contains("验证码")) {
                throw VerifyErrorException()
            } else {
                Response.error("未知错误")
            }
        }
    }

    private fun getAlertString(text: String): String {
        val p = Pattern.compile("alert\\('.*'\\);")
        val m = p.matcher(text)
        if (m.find()) {
            val s = m.group()
            return s.substring(7, s.length - 3)
        }
        return ""
    }

    override fun apply(upstream: Observable<ResponseBody>): ObservableSource<Response<String>> {
        return upstream.map { responseBody -> parse(responseBody.string()) }
    }
}
