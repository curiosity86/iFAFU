package cn.ifafu.ifafu.constant

import androidx.annotation.StringDef
import cn.ifafu.ifafu.data.bean.URL
import cn.ifafu.ifafu.data.bean.ZFApi
import cn.ifafu.ifafu.data.bean.ZFApiList
import cn.ifafu.ifafu.data.entity.User

object Constant {
    const val IFAFU_BASE_URL = "https://api.ifafu.cn"
    const val REPAIR_URL = "http://hq.fafu.edu.cn/agent/app/index.php?i=144&c=entry&do=index&m=maihu_repair"
    const val XFB_URL = "http://cardapp.fafu.edu.cn:8088"
    const val WOOLSEN_BASE_URL = "http://woolsen.cn"
    const val SP_USER_INFO = "user_info"
    const val SP_COOKIE = "cookie"
    const val SP_ELEC = "elec"
    const val SYLLABUS_WIDGET = 410

    const val ACTIVITY_SPLASH = 100
    const val ACTIVITY_MAIN = 101
    const val ACTIVITY_LOGIN = 102
    const val ACTIVITY_SYLLABUS = 103
    const val ACTIVITY_SYLLABUS_ITEM = 104
    const val ACTIVITY_SYLLABUS_SETTING = 105
    const val ACTIVITY_EXAM = 106
    const val ACTIVITY_SCORE = 107
    const val ACTIVITY_SCORE_ITEM = 108
    const val ACTIVITY_SCORE_FILTER = 109
    const val ACTIVITY_REPAIR = 110
    const val ACTIVITY_SETTING = 111

    private val URL_MAP: Map<String, ZFApiList> = mapOf(
            User.FAFU to ZFApiList(User.FAFU, "http://jwgl.fafu.edu.cn/{token}/",
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
            ),
            User.FAFU_JS to ZFApiList(User.FAFU_JS, "http://js.ifafu.cn/",
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
    )

    /**
     * 获取对应Url
     *
     * @param filed [ZFApiList]
     * @param user  user
     * @return url
     */
    fun getUrl(filed: String, user: User): String {
        return (URL_MAP[user.school] ?: error(""))[filed, user]
    }

    private val urls = mapOf(
            "FAFU" to URL(host = "http://jwgl.fafu.edu.cn",
                    login = "default2.aspx",
                    verify = "CheckCode.aspx",
                    main = "xs_main.aspx",
                    exam = Pair("xskscx.aspx", "N121604")),
            "FAFU_JS" to URL(host = "http://js.ifafu.cn",
                    login = "default.aspx",
                    verify = "CheckCode.aspx",
                    main = "xs_main.aspx",
                    exam = Pair("xskscx.aspx", "N121603"))
    )

    fun getURL(@School school: String): URL {
        return urls[school] ?: throw IllegalAccessException("Unknown school")
    }

}

const val DATABASE_NAME = "ifafu_db"

const val IFAFU_BASE_URL = "https://api.ifafu.cn"
const val REPAIR_URL = "http://hq.fafu.edu.cn/agent/app/index.php?i=144&c=entry&do=index&m=maihu_repair"
const val XFB_URL = "http://cardapp.fafu.edu.cn:8088"
const val WOOLSEN_BASE_URL = "http://woolsen.cn"
const val SP_USER_INFO = "user_info"
const val SP_COOKIE = "cookie"
const val SP_ELEC = "elec"
const val SYLLABUS_WIDGET = 410

const val ACTIVITY_SPLASH = 100
const val ACTIVITY_MAIN = 101
const val ACTIVITY_LOGIN = 102
const val ACTIVITY_SYLLABUS = 103
const val ACTIVITY_SYLLABUS_ITEM = 104
const val ACTIVITY_SYLLABUS_SETTING = 105
const val ACTIVITY_EXAM = 106
const val ACTIVITY_SCORE = 107
const val ACTIVITY_SCORE_ITEM = 108
const val ACTIVITY_SCORE_FILTER = 109
const val ACTIVITY_REPAIR = 110
const val ACTIVITY_SETTING = 111

@StringDef(value = [User.FAFU, User.FAFU_JS])
annotation class School