package cn.ifafu.ifafu.mvp.login

import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.http.parser.LoginParamParser
import cn.ifafu.ifafu.data.http.parser.LoginParser
import cn.ifafu.ifafu.data.http.parser.VerifyParser
import cn.ifafu.ifafu.entity.Response
import cn.ifafu.ifafu.entity.User
import cn.ifafu.ifafu.entity.ZFApiList
import cn.ifafu.ifafu.mvp.login.LoginContract.Model
import io.reactivex.Observable

class LoginModel(context: Context) : BaseZFModel(context), Model {

    private val verifyParser: VerifyParser by lazy { VerifyParser(context) }

    init {
        Thread {
            verifyParser.init()
        }.start()
    }

    override fun saveUser(user: User) {
        repository.saveUser(user)
    }

    override fun login(user: User): Observable<Response<String>> {
        return innerLogin(
                School.getUrl(ZFApiList.LOGIN, user),
                School.getUrl(ZFApiList.VERIFY, user),
                user.account,
                user.password,
                LoginParamParser(),
                LoginParser(),
                verifyParser
        )
                .doOnNext {
                    if (it.isSuccess) {
                        user.name = it.body ?: "佚名"
                        repository.saveUser(user)
                    }
                }
    }
}