package cn.ifafu.ifafu.data.new_http

import androidx.annotation.IntDef
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.util.HttpClient
import cn.ifafu.ifafu.util.encode
import okhttp3.*
import java.lang.Exception

class IFHttpClient : HttpClient() {

    fun initToken(user: User): User {
        val urls = Constant.getURL(user.school)
        val url = urls.host
        val httpResp = get(url)
        user.token = when (user.school) {
            User.FAFU -> httpResp.request().url().pathSegments().getOrNull(0)
            User.FAFU_JS -> httpResp.header("Set-Cookie")?.substringBefore(";")
            else -> throw IllegalAccessException("Unknown school")
        } ?: throw Exception("Can't find token")
        return user
    }

    fun getJW(user: User, @Domain domain: Int): Response {
        val headers = getHeader(user, domain)
        val url = getUrl(user, domain)
        return get(url, headers)
    }

    fun postJW(user: User, @Domain domain: Int, params: Map<String, String>): Response {
        val headers = getHeader(user, domain)
        val url = getUrl(user, domain)
        return post(url, headers, params)
    }

    private fun getUrl(user: User, @Domain domain: Int): String {
        val urls = Constant.getURL(user.school)
        val baseUrl = if (user.school == User.FAFU_JS) {
            // http://js.ifafu.cn
            urls.host
        } else {
            // http://jwgl.fafu.edu.cn/(0a3ygt45if4fui3yzk4u4mb)
            urls.host + '/' + user.token
        }
        return when (domain) {
            DEFAULT -> "${baseUrl}/${urls.login}"
            LOGIN -> "${baseUrl}/${urls.login}"
            VERIFY -> "${baseUrl}/${urls.verify}"
            MAIN -> "${baseUrl}/${urls.main}"
            //{base}/{api.first}?xh=3170000000&xm=%CC%CC%CC%CC&gnmkdm={api.second}
            EXAM -> "${baseUrl}/${urls.exam.first}?xh=${user.account}&xm=${user.name.encode()}&gnmkdm=${urls.exam.second}"
            else -> throw IllegalAccessException("Unknown url domain")
        }
    }

    private fun getHeader(user: User, @Domain domain: Int): Headers {
        val builder = Headers.Builder()
        if (user.school == User.FAFU_JS) {
            builder["Cookie"] = user.token
        }
        when (domain) {
            EXAM -> builder["Referer"] = getUrl(user, domain)
        }
        return builder.build()
    }

    companion object {
        const val DEFAULT = 1
        const val LOGIN = 2
        const val VERIFY = 3
        const val MAIN = 4
        const val EXAM = 5
    }

    @MustBeDocumented
    @IntDef(value = [DEFAULT, LOGIN, VERIFY, MAIN, EXAM])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Domain
}