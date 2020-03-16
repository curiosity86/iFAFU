package cn.ifafu.ifafu.data.retrofit

import cn.ifafu.ifafu.data.retrofit.RetrofitManager
import cn.ifafu.ifafu.data.retrofit.interceptor.RetrofitFactory
import cn.ifafu.ifafu.data.retrofit.service.WeatherService
import cn.ifafu.ifafu.data.retrofit.service.XfbService
import cn.ifafu.ifafu.data.retrofit.service.ZhengFangService

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