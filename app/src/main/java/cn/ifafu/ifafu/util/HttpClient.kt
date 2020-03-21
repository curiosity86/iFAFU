package cn.ifafu.ifafu.util

import okhttp3.*

class HttpClient {

    private val client by lazy { OkHttpClient() }

    fun get(url: String, headers: Headers? = null): Response {
        val request = Request.Builder()
                .get()
                .url(url)
        if (headers != null) {
            request.headers(headers)
        }
        return client.newCall(request.build()).execute()
    }

    fun post(url: String, headers: Headers? = null, body: Map<String, String>? = null): Response {
        val formBody = if (body != null) {
            FormBody.Builder()
                    .add(body)
                    .build()
        } else {
            null
        }
        val request = Request.Builder()
                .post(formBody)
                .url(url)
        if (headers != null) {
            request.headers(headers)
        }
        return client.newCall(request.build()).execute()
    }

    private fun FormBody.Builder.add(map: Map<String, String>): FormBody.Builder {
        map.forEach { (key, value) ->
            add(key, value)
        }
        return this
    }

}