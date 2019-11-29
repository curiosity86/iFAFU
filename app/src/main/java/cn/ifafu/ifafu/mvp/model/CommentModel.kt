package cn.ifafu.ifafu.mvp.model

import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.entity.CommentItem
import cn.ifafu.ifafu.data.entity.Response
import cn.ifafu.ifafu.data.entity.ZhengFang
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.CommentParser
import cn.ifafu.ifafu.data.http.parser.CommentParser2
import cn.ifafu.ifafu.data.http.parser.CommentParserJS
import cn.ifafu.ifafu.mvp.contract.CommentContract
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.Observable
import java.net.URLDecoder

class CommentModel(context: Context) : BaseZFModel(context), CommentContract.Model {

    private val token: String
        get() = repository.getToken(getUser().account).token

    override fun getCommentList(): Observable<Response<List<CommentItem>>> {
        val user = getUser()
        val mainUrl = School.getUrl(ZhengFang.MAIN, user)
        val commentUrl = if (user.schoolCode == School.FAFU_JS) {
            mainUrl
        } else {
            School.getUrl(ZhengFang.COMMENT, user)
        }
        val ret = initParams(commentUrl, mainUrl)
                .flatMap { _ ->
                    APIManager.getZhengFangAPI()
                            .getInfo(commentUrl, mainUrl)
                }
        return if (user.schoolCode == School.FAFU_JS) {
            ret.compose(CommentParserJS())
        } else {
            ret.compose(CommentParser())
        }
    }

    override fun commentTeacher(item: CommentItem): Observable<Boolean> {
        val url = "http://jwgl.fafu.edu.cn/(${token})/${item.commentUrl.replace("&amp;", "&")}"
        return APIManager.getZhengFangAPI()
                .initParams(url)
                .compose(CommentParser2())
                .compose(RxUtils.ioToMain())
                .flatMap { params ->
                    params["txt_pjxx"] = ""
                    params["Button1"] = URLDecoder.decode("+%CC%E1+%BD%BB+", "GBK")
                    APIManager.getZhengFangAPI()
                            .post(url, params)
                }
                .map { true }
    }

    override fun getSchoolCode(): Int {
        return getUser().schoolCode
    }

    override fun submit(params: MutableMap<String, String>): Observable<Boolean> {
        val user = getUser()
        return if (user.schoolCode == School.FAFU) {
            val commentUrl = School.getUrl(ZhengFang.COMMENT, user)
            val mainUrl = School.getUrl(ZhengFang.MAIN, user)
            params["btn_tj"] = ""
            APIManager.getZhengFangAPI()
                    .post(commentUrl, mainUrl, params)
                    .map { responseBody -> responseBody.string().contains("完成评价") }
        } else {
            val mainUrl = School.getUrl(ZhengFang.MAIN, user)
            APIManager.getZhengFangAPI()
                    .post(mainUrl, mainUrl, params)
                    .map { responseBody -> responseBody.string().contains("完成评价") }
        }
    }

    override fun getJumpInfo(item: CommentItem): Map<String, String> {
        return if (getUser().schoolCode == School.FAFU) {
            mapOf(
                    "title" to "评价教师【${item.teacherName}】",
                    "url" to "http://jwgl.fafu.edu.cn/(${token})/${item.commentUrl.replace("&amp;", "&")}"
            )
        } else {
            mapOf(
                    "title" to "评价课程【${item.courseName}】",
                    "url" to "http://js.ifafu.cn/${item.commentUrl.replace("&amp;", "&")}"
            )
        }
    }

}
