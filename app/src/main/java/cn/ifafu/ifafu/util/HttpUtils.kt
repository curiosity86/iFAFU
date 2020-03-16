package cn.ifafu.ifafu.util

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object HttpUtils {

    private val client by lazy { OkHttpClient() }

    fun get(url: String): Response {
        val request = Request.Builder()
                .method("GET", null)
                .url(url)
                .build()
        return client.newCall(request).execute()
    }

    fun get(url: String, headers: Map<String, String>? = null): Response {
        val request = Request.Builder()
                .apply {
                    get()
                    url(url)
                    //添加请求头
                    if (headers != null) {
                        for ((key, value) in headers) {
                            addHeader(key, value)
                        }
                    }
                }
                .build()
        return client.newCall(request).execute()
    }

    fun post(url: String, headers: Map<String, String>? = null, body: Map<String, String>? = null): Response {
        val formBody = if (body != null) {
            FormBody.Builder()
                    .apply {
                        for ((key, value) in body) {
                            add(key, value)
                        }
                    }
                    .build()
        } else {
            null
        }
        val request = Request.Builder()
                .apply {
                    post(formBody)
                    url(url)
                    //添加请求头
                    if (headers != null) {
                        for ((key, value) in headers) {
                            addHeader(key, value)
                        }
                    }
                }
                .build()
        return client.newCall(request).execute()
    }

}