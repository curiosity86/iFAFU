package cn.ifafu.ifafu.base.ifafu

import android.content.Context
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BaseModel
import cn.ifafu.ifafu.data.entity.Response
import cn.ifafu.ifafu.data.entity.ZhengFang
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException
import cn.ifafu.ifafu.data.exception.NoAuthException
import cn.ifafu.ifafu.data.exception.VerifyException
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.LoginParamParser
import cn.ifafu.ifafu.data.http.parser.LoginParser
import cn.ifafu.ifafu.data.http.parser.ParamsParser
import cn.ifafu.ifafu.data.http.parser.VerifyParser
import io.reactivex.Observable
import java.net.URLEncoder
import javax.security.auth.login.LoginException

abstract class BaseZFModel(context: Context) : BaseModel(context), IZFModel {

    /**
     * 通过[Observable.retryWhen]捕捉[LoginException]异常后，触发登录账号
     *
     * Need [LoginException]
     */
    protected fun initParams(url: String, referer: String): Observable<MutableMap<String, String>> {
        return APIManager.getZhengFangAPI()
                .initParams(url, referer)
                .compose(ParamsParser())
                .retryWhen {
                    it.flatMap { throwable ->
                        if (throwable is NoAuthException || throwable.message?.contains("302") == true) {
                            println(IFAFU.loginDisposable)
                            val loginDisposable = IFAFU.loginDisposable
                            if (loginDisposable != null && !loginDisposable.isDisposed) {
                                while (!loginDisposable.isDisposed) {
                                    Thread.sleep(100)
                                }
                                Observable.just(true)
                            } else {
                                reLogin()
                            }
                        } else {
                            println("Observable.error(throwable)")
                            Observable.error(throwable)
                        }
                    }
                }
    }

    override fun reLogin(): Observable<Response<String>> {
        return Observable.just(true).flatMap { _ ->
            val user = repository.loginUser ?: return@flatMap Observable.empty<Response<String>>()
            val loginUrl = School.getUrl(ZhengFang.LOGIN, user)
            val verifyUrl = School.getUrl(ZhengFang.VERIFY, user)
            val mainUrl = School.getUrl(ZhengFang.MAIN, user)
            val loginParser = LoginParser()
            APIManager.getZhengFangAPI()
                    .getInfo(mainUrl, null)
                    .compose(loginParser)
                    .flatMap<Response<String>> { stringResponse ->
                        println("Observable.getInfo")
                        if (!stringResponse.isSuccess) {
                            APIManager.getZhengFangAPI()
                                    .getInfo(mainUrl, null)
                                    .compose(loginParser)
                                    .flatMap {
                                        innerLogin(loginUrl,
                                                verifyUrl,
                                                user.account,
                                                user.password,
                                                LoginParamParser(),
                                                loginParser,
                                                VerifyParser(mContext)
                                        )
                                    }
                        } else {
                            Observable.just(stringResponse)
                        }
                    }
                    .map<Response<String>> { response ->
                        when (response.code) {
                            Response.FAILURE -> throw LoginInfoErrorException(response.message)
                            Response.ERROR -> throw Exception(response.message)
                            else -> response
                        }
                    }
                    .doOnNext { resp ->
                        if (resp.isSuccess) {
                            user.name = resp.body
                            repository.saveUser(user)
                        }
                    }
        }
    }

    protected fun innerLogin(loginUrl: String,
                             verifyUrl: String,
                             account: String,
                             password: String,
                             paramsParser: LoginParamParser,
                             loginParser: LoginParser,
                             verifyParser: VerifyParser): Observable<Response<String>> {
        return APIManager.getZhengFangAPI()
                .initParams(loginUrl)
                .compose(paramsParser)
                .map { params ->
                    params["txtUserName"] = account
                    params["Textbox1"] = ""
                    params["TextBox2"] = password
                    params["RadioButtonList1"] = "ѧ��"
                    params["Button1"] = ""
                    params["lbLanguage"] = ""
                    params["hidPdrs"] = ""
                    params["hidsc"] = ""
                    for (entry in params.entries) {
                        if (entry.value.isNotEmpty()) {
                            entry.setValue(URLEncoder.encode(entry.value, "GBK"))
                        }
                    }
                    params
                }
                .flatMap { params ->
                    APIManager.getZhengFangAPI()
                            .getCaptcha(verifyUrl)
                            .compose(verifyParser)
                            .flatMap { verify ->
                                params["txtSecretCode"] = verify
                                APIManager.getZhengFangAPI()
                                        .login(loginUrl, params)
                                        .compose(loginParser)
                            }
                            .retry(10) { throwable -> throwable is VerifyException }
                }

    }

}
