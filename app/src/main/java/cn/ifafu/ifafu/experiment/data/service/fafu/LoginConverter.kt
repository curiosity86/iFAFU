package cn.ifafu.ifafu.experiment.data.service.fafu

import cn.ifafu.ifafu.experiment.bean.IFResponse
import org.jsoup.Jsoup
import java.util.regex.Pattern

class LoginConverter {

    fun convert(html: String): IFResponse<String> {
        val doc = Jsoup.parse(html)
        val ele = doc.getElementById("xhxm")
        if (ele != null) {
            val name = ele.text().replace("同学", "")
            return IFResponse.Success(name)
        } else if (html.contains("输入原密码")) {
            return IFResponse.Success("佚名")
        }
        val script = doc.select("script[language=javascript]")
        return if (script.size < 2) {
            if (html.contains("ERROR")) {
                IFResponse.Failure("教务系统又崩溃了！\n泡杯茶，等待教务系统恢复")
            } else IFResponse.Failure("未知的登录异常(ERROR)")
        } else {
            val s = getAlertString(script[1].html())
            if (s.contains("用户名") || s.contains("密码") || s.contains("验证码")) {
                IFResponse.Failure(s)
            } else if (doc.text().contains("ERROR")) {
                IFResponse.Failure("教务系统又崩溃了！\n泡杯茶，等待教务系统恢复")
            } else {
                IFResponse.Failure("未知的登录异常(Login02)")
            }
        }
    }

    /**
     * 获取Alert弹窗信息
     */
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