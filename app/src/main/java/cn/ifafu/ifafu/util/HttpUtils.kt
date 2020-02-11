package cn.ifafu.ifafu.util

import okhttp3.*

object HttpUtils {

    fun get(url: String): Response {
        val client = OkHttpClient()
        val request = Request.Builder()
                .method("GET", null)
                .url(url)
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
        val client = OkHttpClient()
        val request = Request.Builder()
                .apply {
                    url(url)
                    method("POST", formBody)
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