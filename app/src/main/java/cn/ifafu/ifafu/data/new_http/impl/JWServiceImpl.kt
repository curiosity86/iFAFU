package cn.ifafu.ifafu.data.new_http.impl

import android.graphics.Bitmap
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.bean.URL
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.exception.NoAuthException
import cn.ifafu.ifafu.data.new_http.IFHttpClient
import cn.ifafu.ifafu.data.new_http.bean.IFResponse
import cn.ifafu.ifafu.data.new_http.JWService
import cn.ifafu.ifafu.data.new_http.converter.ExamConverter
import cn.ifafu.ifafu.data.new_http.converter.LoginConverter
import cn.ifafu.ifafu.data.retrofit.parser.VerifyParser
import cn.ifafu.ifafu.util.BitmapUtil
import cn.ifafu.ifafu.util.SPUtils
import cn.ifafu.ifafu.util.encode
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.IOException
import java.util.regex.Pattern
import javax.security.auth.login.LoginException
import kotlin.collections.HashMap

class JWServiceImpl : JWService {

    private var user: User? = null
        set(value) {
            if (field != value) {
                value?.school?.let {
                    urls = Constant.getURL(it)
                }
                field = value
            }
        }

    companion object {
        private const val VERIFY_REPEAT_COUNT = 6 //验证码识别错误重复登录次数
    }

    private val http = IFHttpClient()
    private lateinit var urls: URL

    override suspend fun checkoutTo(user: User) {
        this.user = user
        this.urls = Constant.getURL(user.school)
        reLogin()
    }

    override suspend fun login(
            account: String,
            password: String
    ): IFResult<User> = withContext(Dispatchers.IO) {
        val school = if (account.length == 10) User.FAFU else User.FAFU_JS
        var user = User()
        user.account = account
        user.password = password
        user.school = school
        try {
            user = http.initToken(user)
            loginLazyAsync(user).await().getOrElse(onGet = {
                SPUtils[Constant.SP_COOKIE].putString("cookie", it.token)
                checkoutTo(it)
                IFResult.success(it)
            }, onElse = {
                IFResult.failure<User>(it.message ?: "登录出错")
            })
        } catch (e: IOException) {
            IFResult.failure<User>("登录失败，请检查网络连接")
        } catch (e: Exception) {
            e.printStackTrace()
            IFResult.failure<User>("登录出错")
        }
    }

    override suspend fun reLogin(): IFResult<User> {
        val resp = reLoginInner()
        return resp.getOrElse(onGet = {
            IFResult.success(it)
        }, onElse = {
            if (resp.message?.contains("账号|密码".toRegex()) == true) {
                IFResult.failure<User>(LoginException(resp.message))
            } else {
                IFResult.failure(resp.message ?: "登录出错")
            }
        })
    }

    override suspend fun getNowExams(): IFResult<List<Exam>> = withContext(Dispatchers.IO) {
        try {
            autoReLogin { getExam() }
        } catch (e: Exception) {
            e.printStackTrace()
            IFResult.failure<List<Exam>>(e)
        }
    }


    private fun getExam(): IFResult<List<Exam>> {
        val user = inLoginUser
        return ExamConverter(user)
                .convert(http.getJW(user, IFHttpClient.EXAM))
    }

    //自动重新登录
    private suspend fun <T> autoReLogin(block: () -> IFResult<T>): IFResult<T> {
        return try {
            block()
        } catch (e: NoAuthException) {
//            Timber.d("Auto ReLogin:${user?.account}")
            val resp = reLoginInner()
            if (resp.isSuccess) {
                block()
            } else {
                if (resp.message?.contains("账号|密码".toRegex()) == true) {
                    IFResult.failure(LoginException(resp.message))
                } else {
                    IFResult.failure(resp.message ?: "重新登录出错(unknown)")
                }
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
    private suspend fun reLoginInner(): IFResponse<User> {
        val job = reLoginJob?.takeIf { it.isActive }
                ?: loginLazyAsync(inLoginUser)
        reLoginJob = job
        return job.await()
    }

    private fun loginLazyAsync(user: User): Deferred<IFResponse<User>> = GlobalScope.async(start = CoroutineStart.LAZY) {
        //设置必要参数（异步）
        val paramsAsync = getHiddenParamAsync(
                user, IFHttpClient.LOGIN, mapOf(
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
        //初始化登录解析和验证码识别工具（异步）
        val verifierSync = async {
            VerifyParser(BaseApplication.appContext).apply { init() }
        }
        val loginer = LoginConverter()
        //验证码错误，则重复登录6次
        var repeat = 0
        while (repeat++ < VERIFY_REPEAT_COUNT) {
            //异步获取验证码
            val verifyBitmapSync = getVerifyImageAsync(user)
            //等待验证码工具初始化完成后识别验证码
            val code = verifierSync.await().todo(verifyBitmapSync.await())
            if (params == null) {
                //同步等待初始参数获取
                params = paramsAsync.await()
            }
            params["txtSecretCode"] = code
            val ifResp = loginer.convert(http.postJW(user, IFHttpClient.LOGIN, params))
            ifResp.getOrElse<Unit>(onGet = {
                user.name = ifResp.data ?: "？？？"
                //TODO 金山Cookie临时过渡方案
                if (user.school == User.FAFU_JS) {
                    SPUtils[Constant.SP_COOKIE].putString("cookie", user.token)
                }
                this@JWServiceImpl.user = user
                return@async IFResponse.success(user)
            }, onElse = {
                if (it.isError) {
                    return@async IFResponse.failure<User>(ifResp.message
                            ?: "未知错误信息(Empty)")
                } else if (ifResp.isFailure && ifResp.message?.contains("验证码") == false) {
                    //非验证码错误则return错误信息
                    return@async IFResponse.failure<User>(ifResp.message)
                }
            })
        }
        return@async IFResponse.failure<User>("未知登录错误(End)")
    }

    private fun getVerifyImageAsync(user: User): Deferred<Bitmap?> = GlobalScope.async {
        http.getJW(user, IFHttpClient.VERIFY).body()?.byteStream().use {
            BitmapUtil.bytesToBitmap(it?.readBytes())
        }
    }

    @Throws(IOException::class, IllegalAccessException::class)
    private fun getHiddenParamAsync(user: User, domain: Int, origin: Map<String, String>? = null): Deferred<MutableMap<String, String>> = GlobalScope.async {
        val response = http.getJW(user, domain)
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
}