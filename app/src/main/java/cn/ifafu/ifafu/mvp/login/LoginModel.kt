package cn.ifafu.ifafu.mvp.login

import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.data.http.parser.LoginParamParser
import cn.ifafu.ifafu.data.http.parser.LoginParser
import cn.ifafu.ifafu.data.http.parser.VerifyParser
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
                School.getUrl(ZhengFang.LOGIN, user),
                School.getUrl(ZhengFang.VERIFY, user),
                user.account,
                user.password,
                LoginParamParser(),
                LoginParser(),
                verifyParser
        ).doOnNext {
            if (it.isSuccess) {
                user.name = it.body
                repository.saveUser(user)
                repository.syllabusSetting.run {
                    if (this == null) {
                        val setting = SyllabusSetting(user.account)
                        setting.beginTime = when(user.schoolCode) {
                            School.FAFU -> SyllabusSetting.intBeginTime[0].toList()
                            School.FAFU_JS -> SyllabusSetting.intBeginTime[1].toList()
                            else -> null
                        }
                        repository.saveSyllabusSetting(setting)
                    }
                }
                repository.setting.run {
                    if (this == null) {
                        repository.saveSetting(Setting(user.account))
                    }
                }
            }
        }
    }
}