package cn.ifafu.ifafu.app

import cn.ifafu.ifafu.entity.User
import cn.ifafu.ifafu.entity.ZFApi
import cn.ifafu.ifafu.entity.ZFApiList
import java.util.*

object School {
    const val FAFU = "FAFU"
    const val FAFU_JS = "FAFU_JS"
    private val URL_MAP: MutableMap<String, ZFApiList> = HashMap()
    /**
     * 获取对应Url
     *
     * @param filed [ZFApiList]
     * @param user  user
     * @return url
     */
    fun getUrl(filed: String?, user: User): String {
        return URL_MAP[user.schoolCode]!![filed, user]
    }

    init {
        URL_MAP[FAFU] = ZFApiList(FAFU, "http://jwgl.fafu.edu.cn/{token}/",
                "default2.aspx",
                "CheckCode.aspx",
                "xs_main.aspx",
                mapOf(
                        ZFApiList.SYLLABUS to ZFApi("xskbcx.aspx", "N121602"),
                        ZFApiList.EXAM to ZFApi("xskscx.aspx", "N121604"),
                        ZFApiList.SCORE to ZFApi("xscjcx_dq_fafu.aspx", "N121605"),
                        ZFApiList.COMMENT to ZFApi("xsjxpj2fafu2.aspx", "N121400"),
                        ZFApiList.ELECTIVES to ZFApi("pyjh.aspx", "N121607")
                )
        )
        URL_MAP[FAFU_JS] = ZFApiList(FAFU_JS, "http://js.ifafu.cn/",
                "default.aspx",
                "CheckCode.aspx",
                "xs_main.aspx",
                mapOf(
                        ZFApiList.SYLLABUS to ZFApi("xskbcx.aspx", "N121602"),
                        ZFApiList.EXAM to ZFApi("xskscx.aspx", "N121603"),
                        ZFApiList.SCORE to ZFApi("xscjcx_dq.aspx", "N121613"),
                        ZFApiList.ELECTIVES to ZFApi("pyjh.aspx", "N121606")
                )
        )
    }
}