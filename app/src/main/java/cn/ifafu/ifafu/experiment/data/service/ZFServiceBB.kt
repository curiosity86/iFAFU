package cn.ifafu.ifafu.experiment.data.service

import android.graphics.Bitmap
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.bean.URL
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.retrofit.parser.VerifyParser
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.experiment.data.service.fafu.ExamConverter
import cn.ifafu.ifafu.experiment.data.service.fafu.LoginConverter
import cn.ifafu.ifafu.experiment.data.service.fafu.ScoreConverter
import cn.ifafu.ifafu.util.BitmapUtil
import cn.ifafu.ifafu.util.HttpClient
import cn.ifafu.ifafu.util.encode
import okhttp3.Headers
import org.jsoup.Jsoup
import timber.log.Timber
import java.io.IOException
import java.util.regex.Pattern

/**
 * @param application 初始化验证码识别工具需要
 */
class ZFServiceBB : ZFService {

    private val http = HttpClient()

    private val urls = URL(
            host = "http://jwgl.fafu.edu.cn",
            login = "default2.aspx",
            verify = "CheckCode.aspx",
            main = "xs_main.aspx",
            exam = Pair("xskscx.aspx", "N121604"),
            score = Pair("xscjcx_dq_fafu.aspx", "N121605"))

    private val VERIFY_REPEAT_COUNT = 10

    override fun login(user: User): IFResponse<User> {
        //设置必要参数
        val loginUrl = getUrl(user, URL.LOGIN)
        val params = (when (val resp = getFormHiddenParam(loginUrl, login = true)) {
            is IFResponse.Success -> resp.data
            is IFResponse.Failure -> return resp
            is IFResponse.Error -> return resp
            is IFResponse.NoAuth -> return resp
        } + mapOf(
                "txtUserName" to user.account,
                "Textbox1" to "",
                "TextBox2" to user.password,
                "RadioButtonList1" to "",
                "Button1" to "",
                "lbLanguage" to "",
                "hidPdrs" to "",
                "hidsc" to ""
        )).toMutableMap()
        Timber.d("LoadScoresResource#ZFServiceBB#login => params=${params}")
        //初始化登录解析和验证码识别工具
        val verifier = VerifyParser(BaseApplication.appContext).apply { init() }
        val loginer = LoginConverter()
        var repeat = 0
        loop@ while (repeat++ < VERIFY_REPEAT_COUNT) {
            Timber.d("LoadScoresResource#ZFServiceBB#login")
            //获取验证码并识别
            val verifyBitmap = getVerifyImage(user)
            params["txtSecretCode"] = verifier.todo(verifyBitmap)
            val httpResponse = http.post(loginUrl, body = params)
            val html = httpResponse.body()?.string() ?: return IFResponse.Failure("无返回信息")
            //若验证码错误，则重新获取验证码识别
            when (val resp = loginer.convert(html)) {
                is IFResponse.Failure -> {
                    if (resp.message.contains("验证码")) {
                        continue@loop
                    } else {
                        return resp
                    }
                }
                is IFResponse.Error -> {
                    return resp
                }
                is IFResponse.Success -> {
                    user.name = resp.data
                    return IFResponse.Success(user)
                }
            }
        }
        return IFResponse.Failure("未知登录错误(Login End)")
    }

    override fun fetchScores(user: User, year: String, term: String): IFResponse<List<Score>> {
        Timber.d("LoadScoresResource#ZFServiceBB#fetchScores => year=${year}, term=${term}")
        val url = getUrl(user, URL.SCORE)
        val params = when (val resp = getFormHiddenParam(url, Headers.of("Referer", getUrl(user, URL.MAIN)))) {
            is IFResponse.Success -> resp.data
            is IFResponse.Failure -> return resp
            is IFResponse.Error -> return resp
            is IFResponse.NoAuth -> return resp
        } + mapOf("ddlxn" to year, "ddlxq" to term, "btnCx" to "")
        val httpResponse = http.post(url, headers = Headers.of("Referer", url), body = params)
        val html = httpResponse.body()?.string() ?: return IFResponse.Failure("无返回信息")
        return ScoreConverter().convert(html)
    }

    override fun fetchExams(user: User, year: String, term: String): IFResponse<List<Exam>> {
        val url = getUrl(user, URL.EXAM)
        val headers = Headers.of("Referer", url)
        val params = when (val resp = getFormHiddenParam(url, headers)) {
            is IFResponse.Success -> resp.data
            is IFResponse.Failure -> return resp
            is IFResponse.Error -> return resp
            is IFResponse.NoAuth -> return resp
        } + mapOf("xnd" to year, "xqd" to term)
        val httpResponse = http.post(url, headers = headers, body = params)
        val html = httpResponse.body()?.string() ?: return IFResponse.Failure("无返回信息")
        return ExamConverter().convert(html).run {
            if (this is IFResponse.Success) {
                return if (year == "全部" && term == "全部") {
                    this
                } else if (year == "全部") {
                    this.copy(data = this.data.filter { it.term == term })
                } else if (term == "全部") {
                    this.copy(data = this.data.filter { it.year == year })
                } else {
                    this.copy(data = this.data.filter { it.year == year && it.term == term })
                }
            } else {
                this
            }
        }
    }

    private fun getVerifyImage(user: User): Bitmap? {
        val url = getUrl(user, URL.VERIFY)
        return http.get(url).body()?.byteStream().use {
            BitmapUtil.bytesToBitmap(it?.readBytes())
        }
    }

    private fun getFormHiddenParam(
            url: String,
            headers: Headers? = null,
            login: Boolean = false
    ): IFResponse<Map<String, String>> {
        try {
            val httpResponse = http.get(url, headers)
            val html = httpResponse.body()?.string()
                    ?: return IFResponse.Failure("无法获取到网页")
            if (!login && html.contains("请登录") ||
                    html.contains("请重新登陆") ||
                    html.contains("302 Found")) {
                return IFResponse.NoAuth
            }
            val pattern = Pattern.compile("alert\\('.*'\\);")
            val matcher = pattern.matcher(html)
            if (matcher.find()) {
                val text = matcher.group()
                if (text.matches("现在不能查询".toRegex())) {
                    //返回Alert弹窗信息
                    return IFResponse.Failure(text.substring(7, text.length - 3))
                }
            }
            val params = HashMap<String, String>()
            val document = Jsoup.parse(html)
            val elements = document.select("input[type=\"hidden\"]")
            for (element in elements) {
                params[element.attr("name")] = element.attr("value")
            }
            Timber.d("LoadScoresResource#ZFServiceBB#getFormHiddenParam => url=${url}, check=${login}")
            return IFResponse.Success(params)
        } catch (e: IOException) {
            return IFResponse.Failure("网络异常")
        }
    }

    private fun getUrl(user: User, domain: String): String {
        // http://jwgl.fafu.edu.cn/(0a3ygt45if4fui3yzk4u4mb)
        val baseUrl = urls.host + '/' + user.token
        return when (domain) {
            URL.DEFAULT -> "${baseUrl}/${urls.login}"
            URL.LOGIN -> "${baseUrl}/${urls.login}"
            URL.VERIFY -> "${baseUrl}/${urls.verify}"
            URL.MAIN -> "${baseUrl}/${urls.main}?xh=${user.account}"
            //http://jwgl.fafu.edu.cn/(0a3ygt45if4fui3yzk4u4mb)/xskscx.aspx?xh=3170000000&xm=%CC%CC%CC%CC&gnmkdm=N121604
            URL.EXAM ->
                "${baseUrl}/${urls.exam.first}?xh=${user.account}&xm=${user.name.encode()}&gnmkdm=${urls.exam.second}"
            URL.SCORE ->
                "${baseUrl}/${urls.score.first}?xh=${user.account}&xm=${user.name.encode()}&gnmkdm=${urls.score.second}"
            else -> throw IllegalAccessException("Unknown url domain")
        }
    }

}