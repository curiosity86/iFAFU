package cn.ifafu.ifafu.data.retrofit.interceptor

import cn.ifafu.ifafu.util.SPUtils
import okhttp3.Interceptor
import okhttp3.Response

class JWCookieInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val cookie = SPUtils["Cookie"].getString("cookie")
        val request = if (cookie.isNotBlank()) {
            chain.request().newBuilder()
                    .addHeader("Cookie", cookie)
                    .build()
        } else {
            chain.request()
        }
        val response = chain.proceed(request)
        val cookieString = response.header("Set-Cookie")
        if (cookieString != null) {
            val cookies = cookieString.substringBefore(";")
            SPUtils["Cookie"].putString("cookie", cookies)
        }
        return response
    }
}