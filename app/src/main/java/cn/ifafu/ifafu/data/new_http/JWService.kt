package cn.ifafu.ifafu.data.new_http

import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.bean.Version
import cn.ifafu.ifafu.data.bean.Weather
import cn.ifafu.ifafu.data.entity.*
import javax.security.auth.login.LoginException

interface JWService {
    suspend fun checkoutTo(user: User)
    suspend fun login(account: String, password: String): IFResult<User>

    /**
     * @throws LoginException 账号密码错误
     */
    suspend fun reLogin(): IFResult<User>
    suspend fun getNowExams(): IFResult<List<Exam>>

}