package cn.ifafu.ifafu.data.new_http

import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.bean.Version
import cn.ifafu.ifafu.data.bean.Weather

interface WoService {

    suspend fun getNewVersion(): IFResult<Version>

    /**
     * 获取开学日期
     * @return 2020-02-16
     */
    suspend fun getOpeningDay(): IFResult<String>

    suspend fun getWeather(code: String): IFResult<Weather>
}