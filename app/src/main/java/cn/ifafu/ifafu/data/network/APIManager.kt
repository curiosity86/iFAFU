package cn.ifafu.ifafu.data.network

import cn.ifafu.ifafu.data.network.elec.RetrofitFactory
import cn.ifafu.ifafu.data.network.service.WeatherService
import cn.ifafu.ifafu.data.network.service.XfbService
import cn.ifafu.ifafu.data.network.service.ZhengFangService

object APIManager {
    val weatherAPI: WeatherService by lazy {
        RetrofitManager.obtainService(WeatherService::class.java)
    }

    val zhengFangAPI: ZhengFangService by lazy {
        RetrofitManager.obtainService(ZhengFangService::class.java)
    }

    val xfbAPI: XfbService by lazy {
        RetrofitFactory.obtainService(XfbService::class.java)
    }

}