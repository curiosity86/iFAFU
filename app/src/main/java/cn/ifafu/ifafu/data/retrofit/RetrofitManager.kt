package cn.ifafu.ifafu.data.retrofit

import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.retrofit.interceptor.JWCookieInterceptor
import cn.ifafu.ifafu.util.SPUtils.Companion.get
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.*
import java.util.concurrent.TimeUnit

object RetrofitManager {
    private const val CONNECT_TIME_OUT = 15 //连接超时时长x秒
    private const val READ_TIME_OUT = 15 //读数据超时时长x秒
    private const val WRITE_TIME_OUT = 15 //写数据接超时时长x秒

    private val cookieList: MutableList<String> = ArrayList()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(Constant.IFAFU_BASE_URL)
                .client(okHttpClient)
                .build()
    }

    init {
        val cookieMap = get(Constant.SP_COOKIE).all as Map<String, String>
        cookieList.addAll(cookieMap.values)
    }

    @JvmStatic
    fun <T> obtainService(clazz: Class<T>): T {
        return retrofit.create(clazz)
    }

    private val okHttpClient: OkHttpClient
        get() = OkHttpClient.Builder()
                .addInterceptor(cookieInterceptor)
                .connectTimeout(CONNECT_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

    private val cookieInterceptor: Interceptor by lazy { JWCookieInterceptor() }

}