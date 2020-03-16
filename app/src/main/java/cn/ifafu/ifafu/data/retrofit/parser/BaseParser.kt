package cn.ifafu.ifafu.data.retrofit.parser

import cn.ifafu.ifafu.data.exception.NoAuthException
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import java.io.BufferedReader
import java.io.InputStreamReader

abstract class BaseParser<T> : ObservableTransformer<ResponseBody, T> {

    @Throws(Exception::class)
    abstract fun parse(html: String): T

    protected fun getAccount(document: Document): String {
        val e = document.select("span[id=\"Label5\"]")
        return e.text().replace("学号：", "")
    }


    override fun apply(upstream: Observable<ResponseBody>): ObservableSource<T> {
        return upstream.map { responseBody ->
            val `is` = responseBody.byteStream()
            val fReader = InputStreamReader(`is`, "GBK")
            val sr = BufferedReader(fReader)
            val html = StringBuilder()
            var s: String?
            while (true) {
                s = sr.readLine()
                if (s == null) break
                if (s.contains("请登录") || s.contains("请重新登陆") || s.contains("302 Found")) {
                    throw NoAuthException()
                }
                html.append(s)
            }
            parse(html.toString())
        }
    }

    protected fun check(html: String) {
        if (html.contains("请登录") || html.contains("请重新登陆") || html.contains("302 Found")) {
            throw NoAuthException()
        }
    }
}
