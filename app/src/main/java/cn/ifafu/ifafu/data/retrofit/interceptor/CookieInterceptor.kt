package cn.ifafu.ifafu.data.retrofit.interceptor

import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.entity.ElecCookie
import com.alibaba.fastjson.JSONObject
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CookieInterceptor : Interceptor {

    private var cookie: ElecCookie? = null

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        if (cookie == null) {
            cookie = Repository.XfbRt.getElecCookie()
            println(JSONObject.toJSONString(cookie))
        }
        builder.addHeader("Cookie", cookie!!.toCookieString())
        val response = chain.proceed(builder.build())
        if (response.headers("Set-Cookie").isNotEmpty()) {
            for (header in response.headers("Set-Cookie")) {
                println(header)
                val kv = header.substring(0, header.indexOf(";")).split("=").toTypedArray()
                println("Cookie put: " + kv.contentToString())
                if (kv.size < 2) {
                    cookie!![kv[0]] = ""
                } else {
                    cookie!![kv[0]] = kv[1]
                }
            }
            Repository.XfbRt.saveElecCookie(cookie!!)
        }
        return response
    }
}