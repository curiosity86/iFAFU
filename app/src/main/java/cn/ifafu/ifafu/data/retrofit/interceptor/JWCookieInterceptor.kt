package cn.ifafu.ifafu.data.retrofit.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class JWCookieInterceptor: Interceptor {

    private val mCookies = ArrayList<String>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = if (mCookies.size > 0) {
            chain.request().newBuilder()
                    .addHeader("Cookie", mCookies[0])
                    .build()
        } else {
            chain.request()
        }
        val response = chain.proceed(request)
        val cookieString = response.header("Set-Cookie")
        if (cookieString != null) {
            val cookies = cookieString.split(";")
            for (i in 0 until cookies.size - 1) {
                if (cookies[i].isNotEmpty()) {
                    mCookies.add(cookies[i])
                }
            }
        }
        return response
    }
}