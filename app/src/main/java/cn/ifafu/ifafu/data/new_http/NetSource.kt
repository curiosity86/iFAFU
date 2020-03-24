package cn.ifafu.ifafu.data.new_http

import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.bean.Weather
import cn.ifafu.ifafu.data.entity.*

interface NetSource {
    suspend fun checkoutTo(user: User)
    suspend fun login(account: String, password: String): IFResult<User>
    suspend fun reLogin(): IFResult<User>

    /**
     * 获取开学日期
     * @return 2020-02-16
     */
    suspend fun getOpeningDay(): IFResult<String>

    suspend fun getExams(): IFResult<List<Exam>>

    suspend fun getWeather(code: String): IFResult<Weather>
}