package cn.ifafu.ifafu.data.newly

import android.graphics.Bitmap
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.bean.URL
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.exception.NoAuthException
import cn.ifafu.ifafu.data.newly.converter.ExamConverter
import cn.ifafu.ifafu.data.newly.converter.LoginConverter
import cn.ifafu.ifafu.data.retrofit.parser.VerifyParser
import cn.ifafu.ifafu.util.BitmapUtil
import cn.ifafu.ifafu.util.HttpClient
import cn.ifafu.ifafu.util.encode
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.*
import okhttp3.Headers
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

private const val VERIFY_REPEAT_COUNT = 6 //验证码识别错误重复登录次数

@Suppress("BlockingMethodInNonBlockingContext")
class HttpSourceImpl(private var user: User? = null) : HttpSource {

    private val http = HttpClient()
    private lateinit var urls: URL

    init {
        user?.run {
            urls = Constant.getURL(schoolCode)
        }
    }

    override suspend fun login(
            account: String,
            password: String
    ): IFResult<User> = withContext(Dispatchers.IO) {
        val school = if (account.length == 10) Constant.FAFU else Constant.FAFU_JS
        val user = User()
        user.account = account
        user.password = password
        user.schoolCode = school

        try {
            val ifResp = loginLazyAsync(user).await()
            if (ifResp.isSuccess) {
                switch(ifResp.data!!)
                IFResult.success(ifResp.data)
            } else {
                IFResult.failure<User>(ifResp.message ?: "登录错误")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            IFResult.failure<User>("登录错误")
        }
    }

    override suspend fun getOpeningDay(): IFResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = http.get("${Constant.WOOLSEN_BASE_URL}/api/text/OpeningDay")
            val wResp = JSONObject.parseObject(response.body()?.string() ?: "", WoResponse::class.java)
            IFResult.success(wResp.data as String)
        } catch (e: JSONException) {
            IFResult.failure<String>("开学时间获取出错")
        } catch (e: Exception) {
            IFResult.failure<String>(e)
        }
    }

    override suspend fun getExam(): IFResult<List<Exam>> = withContext(Dispatchers.IO) {
        try {
            autoReLogin { getExamAsync() }
        } catch (e: Exception) {
            e.printStackTrace()
            IFResult.failure<List<Exam>>(e)
        }
    }

    override suspend fun switch(user: User) {
        this.user = user
        this.urls = Constant.getURL(user.schoolCode)
    }

    private fun getExamAsync(): IFResult<List<Exam>> {
        val user = inLoginUser
        return ExamConverter(user)
                .convert(http.get(
                        url = getUrl(user, urls.exam),
                        headers = Headers.of("Referer", getReferer(user))
                ))
    }

    //自动重新登录
    private suspend fun <T> autoReLogin(block: () -> IFResult<T>): IFResult<T> {
        return try {
            block()
        } catch (e: NoAuthException) {
            val resp = reLogin()
            if (resp.isSuccess) {
                block()
            } else {
                IFResult.failure(resp.message ?: "重新登录出错(unknown)")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            IFResult.failure("网络异常")
        }
    }

    private val inLoginUser: User //正在使用的账号
        get() = user ?: throw IllegalAccessException("login user is null")

    private var reLoginJob: Deferred<IFResponse<User>>? = null

    /* 重新登录 */
    private suspend fun reLogin(): IFResponse<User> {
        val job = reLoginJob?.takeIf { it.isCompleted }
                ?: loginLazyAsync(inLoginUser)
        reLoginJob = job
        return job.await()
    }

    private fun loginLazyAsync(user: User): Deferred<IFResponse<User>> = GlobalScope.async(start = CoroutineStart.LAZY) {
        val url = Constant.getURL(user.schoolCode)
        val token: String
        val baseUrl: String
        if (user.schoolCode == Constant.FAFU) {
            token = generateToken()
            baseUrl = url.host + token + "/"
        } else {
            token = ""
            baseUrl = url.host
        }
        //设置必要参数
        val paramsAsync = getHiddenParamAsync(
                baseUrl, mapOf(
                "txtUserName" to user.account.encode("GBK"),
                "Textbox1" to "",
                "TextBox2" to user.password.encode("GBK"),
                "RadioButtonList1" to "",
                "Button1" to "",
                "lbLanguage" to "",
                "hidPdrs" to "",
                "hidsc" to ""
        ))
        var params: MutableMap<String, String>? = null
        //初始化登录解析和验证码识别工具
        val verifierSync = async {
            VerifyParser(BaseApplication.appContext).apply { init() }
        }
        var verifier: VerifyParser? = null
        val loginer = LoginConverter()
        //验证码错误，则重复登录6次
        var repeat = 0
        while (repeat++ < VERIFY_REPEAT_COUNT) {
            //异步获取验证码
            val verifyBitmapSync = getVerifyImageAsync(baseUrl + url.verify)
            if (verifier == null) {
                //等待验证码工具初始化完成
                verifier = verifierSync.await()
            }
            val code = verifier.todo(verifyBitmapSync.await())
            if (params == null) {
                //等待初始参数获取
                params = paramsAsync.await()
            }
            params["txtSecretCode"] = code
            val ifResp = loginer.convert(http.post(baseUrl + url.login, body = params))
            if (ifResp.isSuccess) {
                user.name = ifResp.data ?: "？？？"
                user.token = token
                return@async IFResponse.success(user)
            } else if (ifResp.isError) {
                return@async IFResponse.failure<User>(ifResp.message ?: "未知错误信息(Empty)")
            } else if (ifResp.isFailure && ifResp.message?.contains("验证码") == false) {
                return@async IFResponse.failure<User>(ifResp.message)
            }
        }
        return@async IFResponse.failure<User>("未知登录错误(End)")
    }

    private fun getVerifyImageAsync(url: String): Deferred<Bitmap?> = GlobalScope.async {
        return@async http.get(url).body()?.run {
            BitmapUtil.bytesToBitmap(bytes())
        }
    }

    private fun generateToken(): String {
        val randomStr = "abcdefghijklmnopqrstuvwxyz12345".toCharArray()
        val token = StringBuilder()
        val random = Random()
        for (i in 0..23) {
            token.append(randomStr[random.nextInt(31)])
        }
        return "/($token)"
    }

    @Throws(IOException::class, IllegalAccessException::class)
    private fun getHiddenParamAsync(url: String, origin: Map<String, String>? = null): Deferred<MutableMap<String, String>> = GlobalScope.async {
        val response = http.get(url)
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
        val params = origin?.toMutableMap() ?: HashMap()
        val document = Jsoup.parse(html)
        val elements = document.select("input[type=\"hidden\"]")
        for (element in elements) {
            params[element.attr("name")] = element.attr("value")
        }
        return@async params
    }

    //http://jwgl.fafu.edu.cn/(0a3ygt45if4fui3yzk4u4mb)/api.first?xh=3170000000&xm=%CC%CC%CC%CC&gnmkdm=api.second
    private fun getUrl(user: User, api: Pair<String, String>): String {
        return "${getBaseUrl(user)}${api.first}?xh=${user.account}&xm=${user.name.encode()}&gnmkdm=${api.second}"
    }

    // http://jwgl.fafu.edu.cn/(0a3ygt45if4fui3yzk4u4mb)/xs_main.aspx?xh=3170000000
    private fun getReferer(user: User): String {
        return "${getBaseUrl(user)}${urls.main}?xh=${user.account}"
    }

    // http://jwgl.fafu.edu.cn/(0a3ygt45if4fui3yzk4u4mb)/
    private fun getBaseUrl(user: User): String {
        return "${urls.host}${user.token}"
    }

}