package cn.ifafu.ifafu.data.http

import cn.ifafu.ifafu.data.http.elec.RetrofitFactory
import cn.ifafu.ifafu.data.http.service.WeatherService
import cn.ifafu.ifafu.data.http.service.XfbService
import cn.ifafu.ifafu.data.http.service.ZhengFangService

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