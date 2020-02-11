package cn.ifafu.ifafu.data.network

import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.util.SPUtils.Companion.get
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
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

    private val cookieInterceptor: Interceptor
        get() = Interceptor { chain: Interceptor.Chain ->
            val request = if (cookieList.size > 0) {
                get(Constant.SP_COOKIE).putString("ASP.NET_SessionId", cookieList[0])
                chain.request().newBuilder()
                        .addHeader("Cookie", cookieList[0])
                        .build()
            } else {
                chain.request()
            }
            val response = chain.proceed(request)
            val cookieString = response.header("Set-Cookie")
            if (cookieString != null) {
                val cookies = cookieString.split(";").toTypedArray()
                for (i in 0 until cookies.size - 1) {
                    if (cookies[i].isNotEmpty()) {
                        cookieList.add(cookies[i])
                    }
                }
            }
            response
        }

}