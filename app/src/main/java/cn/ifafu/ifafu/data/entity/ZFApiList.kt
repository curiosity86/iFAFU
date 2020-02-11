package cn.ifafu.ifafu.data.entity

import com.alibaba.fastjson.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class ZFApiList(
        private val schoolCode: String,
        private val baseUrl: String,
        private val login: String,
        private val verify: String,
        private val main: String,
        private val apiMap: Map<String, ZFApi>
) {

    private var baseUrlTemp: String? = null
    private var accountTemp: String? = null
    private fun getBaseUrl(user: User): String? {
        if (baseUrlTemp == null || user.account != accountTemp) {
            baseUrlTemp = baseUrl
                    .replace("{token}", user.token)
            accountTemp = user.account
        }
        return baseUrlTemp
    }

    operator fun get(filed: String?, user: User): String {
        return when (filed) {
            VERIFY -> String.format("%s%s", getBaseUrl(user), verify)
            LOGIN -> String.format("%s%s", getBaseUrl(user), login)
            MAIN -> String.format("%s%s?xh=%s", getBaseUrl(user), main, user.account)
            else -> try {
                val api = apiMap[filed]
                if (api != null) {
                    String.format("%s%s?xh=%s&xm=%s&gnmkdm=%s",
                            getBaseUrl(user), api.api, user.account, URLEncoder.encode(user.name, "GBK"), api.gnmkdm)
                } else {
                    throw IllegalArgumentException("url is not found")
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                ""
            }
        }
    }

    override fun toString(): String {
        return "ZhengFang{" +
                "schoolCode=" + schoolCode +
                ", baseUrl='" + baseUrl + '\'' +
                ", login='" + login + '\'' +
                ", verify='" + verify + '\'' +
                ", main='" + main + '\'' +
                ", baseUrlTemp='" + baseUrlTemp + '\'' +
                ", apiMap=" + JSONObject.toJSONString(apiMap) +
                '}'
    }

    companion object {
        const val LOGIN = "LOGIN"
        const val VERIFY = "VERIFY"
        const val MAIN = "MAIN"
        const val SYLLABUS = "SYLLABUS"
        const val EXAM = "EXAM"
        const val SCORE = "SCORE"
        const val COMMENT = "COMMENT"
        const val ELECTIVES = "ELECTIVES"
    }

}