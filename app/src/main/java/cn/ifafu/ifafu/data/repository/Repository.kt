package cn.ifafu.ifafu.data.repository

import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.bean.Version
import cn.ifafu.ifafu.ui.main.bean.Weather
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.User
import javax.security.auth.login.LoginException

/**
 * get***FromDbOrNet: 若数据库信息为空，则从教务管理系统获取
 */
interface Repository {

    /**
     * @throws LoginException 账号密码错误
     */
    suspend fun checkoutTo(user: User)

    suspend fun login(account: String, password: String): IFResult<User>

    suspend fun getNotExamsFromDbOrNet(): IFResult<List<Exam>>
    suspend fun getExamsFromDbOrNet(year: String, term: String): IFResult<List<Exam>>


    /**
     * @param code 101230101:福州
     */
    suspend fun getWeather(code: String): IFResult<Weather>
    suspend fun getOpeningDay(): IFResult<String>
    suspend fun getNewVersion(): IFResult<Version>
    suspend fun postFeedback(message: String, contact: String): IFResult<String>
}