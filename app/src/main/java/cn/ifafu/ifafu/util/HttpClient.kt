package cn.ifafu.ifafu.util

import okhttp3.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
open class HttpClient {

    private val client = OkHttpClient()

    fun get(url: String, headers: Headers? = null): Response {
        val request = Request.Builder()
                .get()
                .url(url)
        if (headers != null) {
            val headersBuilder = headers.newBuilder()
            if (headersBuilder["Content-Type"] == null) {
                headersBuilder.set("Content-Type", "application/x-www-form-urlencoded")
            }
            request.headers(headersBuilder.build())
        }
        return client.newCall(request.build()).execute()
    }

    fun post(url: String, headers: Headers? = null, body: Map<String, String>? = null, enc: String? = null): Response {
        val formBody = if (body != null) {
            FormBody.Builder().apply {
                if (enc != null) {
                    add(body, "gb2312")
                } else {
                    addEncoded(body)
                }
            }.build()
        } else null
        val request = Request.Builder().apply {
            post(formBody)
            url(url)
            if (headers != null) {
                headers(headers)
            }
        }.build()
        return client.newCall(request).execute()
    }

}

fun FormBody.Builder.add(map: Map<String, String>, enc: String): FormBody.Builder {
    map.forEach { (key, value) ->
        add(key, value.encode(enc))
    }
    return this
}

fun FormBody.Builder.addEncoded(map: Map<String, String>): FormBody.Builder {
    map.forEach { (key, value) ->
        add(key, value)
    }
    return this
}
