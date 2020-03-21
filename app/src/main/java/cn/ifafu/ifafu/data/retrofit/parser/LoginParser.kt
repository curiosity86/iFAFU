package cn.ifafu.ifafu.data.retrofit.parser

import cn.ifafu.ifafu.data.bean.Response
import cn.ifafu.ifafu.data.exception.VerifyException
import org.jsoup.Jsoup
import java.util.regex.Pattern

class LoginParser : BaseParser<Response<String>>() {
    /**
     * @param html 网页信息
     * @return [Response.SUCCESS] 登录成功 body = user only with name
     * [Response.FAILURE] 信息错误 msg = return msg
     * [Response.ERROR]   服务器错误  msg = error msg
     */
    @Throws(VerifyException::class)
    override fun parse(html: String): Response<String> {
        val doc = Jsoup.parse(html)
        val ele = doc.getElementById("xhxm")
        if (ele != null) {
            val name = ele.text().replace("同学", "")
            return Response.success(name)
        } else if (html.contains("输入原密码")) {
            return Response.success("佚名")
        }
        val script = doc.select("script[language=javascript]")
        return if (script.size < 2) {
            if (html.contains("ERROR")) {
                Response.error("教务系统又崩溃了！")
            } else Response.error("网络异常 0x001")
        } else {
            val s = getAlertString(script[1].html())
            if (s.contains("用户名") || s.contains("密码")) {
                Response.failure(s)
            } else if (doc.text().contains("ERROR")) {
                Response.error("教务系统又崩溃了！")
            } else if (s.contains("验证码")) {
                throw VerifyException()
            } else {
                Response.error("网络异常 0x002")
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
}