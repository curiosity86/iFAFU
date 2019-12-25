package cn.ifafu.ifafu.data.http.elec

import cn.ifafu.ifafu.data.RepositoryImpl
import cn.ifafu.ifafu.entity.ElecCookie
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CookieInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        var cookie: ElecCookie? = RepositoryImpl.getElecCookie()
        if (cookie != null) {
            builder.addHeader("Cookie", cookie.toCookieString())
        }
        val response = chain.proceed(builder.build())
        if (response.headers("Set-Cookie").isNotEmpty()) {
            if (cookie == null) {
                cookie = ElecCookie()
                cookie.account = RepositoryImpl.account
            }
            for (header in response.headers("Set-Cookie")) {
                println(header)
                val kv = header.substring(0, header.indexOf(";")).split("=").toTypedArray()
                println("Cookie put: " + kv.contentToString() + ", Header: " + header)
                if (kv.size < 2) {
                    cookie[kv[0]] = ""
                } else {
                    cookie[kv[0]] = kv[1]
                }
            }
            RepositoryImpl.saveElecCookie(cookie)
        }
        return response
    }
}