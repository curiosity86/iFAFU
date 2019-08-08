package cn.ifafu.ifafu.http

import java.util.HashMap

import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object RetrofitManager {

    private val retrofitMap = HashMap<String, Retrofit>()

    fun <T> obtainService(clazz: Class<T>, baseUrl: String?): T =
            getRetrofit(baseUrl, false).create(clazz)

    fun <T> obtainServiceTemp(clazz: Class<T>, baseUrl: String?): T =
            getRetrofit(baseUrl, true).create(clazz)

    fun clearRetrofit() {
        retrofitMap.clear()
    }

    private fun getRetrofit(baseUrl: String?, temp: Boolean): Retrofit =
            retrofitMap[baseUrl].run {
                if (this != null) return this
                val retrofit = Retrofit.Builder().run {
                    if (!baseUrl.isNullOrBlank()) {
                        baseUrl(baseUrl)
                    }
                    addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    build()
                }
                if (!temp && baseUrl != null) {
                    retrofitMap[baseUrl] = retrofit
                }
                return retrofit
            }
}
