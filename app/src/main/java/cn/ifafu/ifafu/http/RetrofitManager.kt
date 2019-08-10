package cn.ifafu.ifafu.http

import java.util.HashMap

import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException

object RetrofitManager {

    private val retrofitMap = HashMap<String, Retrofit>()

    private val okHttpClient by lazy {
        OkHttpClient().newBuilder()
//                .followRedirects(false)  //禁制OkHttp的重定向操作，我们自己处理重定向
//                .followSslRedirects(false)
                .build();
    }

    fun <T> obtainService(clazz: Class<T>, baseUrl: String): T =
            getRetrofit(baseUrl).create(clazz)

    fun <T> obtainServiceTemp(clazz: Class<T>, baseUrl: String): T =
            getRetrofit(baseUrl).create(clazz)

    fun clearRetrofit() {
        retrofitMap.clear()
    }

    private fun getRetrofit(baseUrl: String): Retrofit =
            retrofitMap[baseUrl].run {
                if (this != null) return this
                val retrofit = Retrofit.Builder().run {
                    baseUrl(baseUrl)
                    client(okHttpClient)
                    addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    build()
                }
                retrofitMap[baseUrl] = retrofit
                return retrofit
            }

}
