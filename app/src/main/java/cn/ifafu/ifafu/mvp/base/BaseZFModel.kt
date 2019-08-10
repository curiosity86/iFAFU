package cn.ifafu.ifafu.mvp.base

import android.content.Context

import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.Response
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.http.RetrofitManager
import cn.ifafu.ifafu.http.exception.VerifyErrorException
import cn.ifafu.ifafu.http.parser.LoginParser
import cn.ifafu.ifafu.http.service.ZhengFangService
import cn.ifafu.ifafu.http.parser.VerifyParser
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

open class BaseZFModel(context: Context) : BaseModel(context), IZFModel {

    protected var mUser: User = IFAFU.getUser()

    protected val zhengFang by lazy { RetrofitManager.obtainService(ZhengFangService::class.java, getBaseUrl(user)) }

    override fun login(user: User): Observable<Response<String>> {
        val verifyParser = VerifyParser(mContext)
        return getVerifyResp()
                .compose(verifyParser) // 识别验证码
                .flatMap { verify -> login(user, verify) } // 登录
                .retry(10) { throwable -> throwable is VerifyErrorException } // 验证码错误重试，最多重试10次;
    }

    /**
     * 登录
     * 验证码错误重试，最多重试10次
     *
     * @return {@link Response#SUCCESS} 登录成功    body = 学生名字
     *         {@link Response#FAILURE} 信息错误    msg = 返回信息
     *         {@link Response#ERROR}   服务器错误  msg = 返回信息
     */
    private fun login(user: User, verify: String): Observable<Response<String>> {
        return zhengFang.login(user.account, user.password, verify,
                "", "", "", "%D1%A7%C9%FA")
                .compose(LoginParser())
                .map { response -> // 验证码错误抛异常用于重试
                    if (response.code == Response.FAILURE && response.message.contains("验证码")) {
                        throw VerifyErrorException()
                    }
                    response
                }
    }

    override fun isTokenAlive(user: User): Observable<Boolean> {
        return Observable
                .create<Boolean> { emitter ->
                    val zhengFang = RetrofitManager.obtainService(ZhengFangService::class.java, getBaseUrl(user))
                    val body = zhengFang.mainHtml(user.account).execute().body()
                    if (body == null) {
                        emitter.onNext(false)
                    } else {
                        emitter.onNext(body.string().contains("欢迎您"))
                    }
                    emitter.onComplete()
                }
                .subscribeOn(Schedulers.io())
    }

    private fun getVerifyResp(): Observable<ResponseBody> {
        return Observable.fromCallable {
            zhengFang.captcha.execute().body()
        }
    }

    private fun getLoginReferer(user: User): String {
        return when (user.schoolCode) {
            Constant.FAFU -> Constant.URL_FAFU + user.token + "default2.aspx"
            Constant.FAFU_JS -> Constant.URL_FAFU_JS + "default.aspx"
            else -> ""
        }
    }

    protected fun getReferer(user: User): String {
        return getBaseUrl(user) + "xs_main.aspx?xh=" + user.account
    }

    override fun getUser(): User {
        return mUser
    }

    protected fun getBaseUrl(user: User): String {
        return when (user.schoolCode) {
            Constant.FAFU -> Constant.URL_FAFU + user.token
            Constant.FAFU_JS -> Constant.URL_FAFU_JS
            else -> ""
        }
    }

}
