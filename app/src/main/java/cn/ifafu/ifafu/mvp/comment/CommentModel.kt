package cn.ifafu.ifafu.mvp.comment

import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.RepositoryImpl
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.CommentParser
import cn.ifafu.ifafu.data.http.parser.CommentParser2
import cn.ifafu.ifafu.data.http.parser.CommentParserJS
import cn.ifafu.ifafu.data.http.parser.CommentParserJS2
import cn.ifafu.ifafu.entity.CommentItem
import cn.ifafu.ifafu.entity.Response
import cn.ifafu.ifafu.entity.User
import cn.ifafu.ifafu.entity.ZhengFang
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.util.encode
import io.reactivex.Observable
import java.net.URLDecoder

class CommentModel(context: Context) : BaseZFModel(context), CommentContract.Model {

    private val token: String
        get() = repository.getToken(getUser()!!.account)!!.token

    private var commitUrlJS = ""

    override fun getCommentList(): Observable<Response<List<CommentItem>>> {
        var user: User? = null
        var mainUrl = ""
        var commentUrl = ""
        return Observable.fromCallable {
            user = getUser()
            mainUrl = School.getUrl(ZhengFang.MAIN, user)
            commentUrl = if (user!!.schoolCode == School.FAFU_JS) {
                mainUrl
            } else {
                School.getUrl(ZhengFang.COMMENT, user)
            }
        }
                .flatMap { initParams(commentUrl, mainUrl) }
                .flatMap { _ ->
                    APIManager.getZhengFangAPI()
                            .getInfo(commentUrl, mainUrl)
                }
                .flatMap {
                    if (user!!.schoolCode == School.FAFU_JS) {
                        Observable.fromCallable { it }.compose(CommentParserJS())
                                .map { response ->
                                    if (!response.isSuccess) {
                                        throw Exception(response.message)
                                    }
                                    val item = response.body.firstOrNull()
                                    if (item != null) {
                                        val referer = "http://js.ifafu.cn/"
                                        val url = "http://js.ifafu.cn/" + item.commentUrl
                                        commitUrlJS = url
                                        println("url = $url")
                                        val done = APIManager.getZhengFangAPI()
                                                .initParams(url, referer)
                                                .map {
                                                    val html = it.string()
                                                    html.contains("所有评价已完成")
                                                }.blockingFirst()
                                        if (done) {
                                            response.body.forEach {
                                                it.isDone = done
                                            }
                                        }
                                    }
                                    response
                                }
                    } else {
                        Observable.fromCallable { it }.compose(CommentParser())
                    }
                }
    }

    override fun commentTeacher(item: CommentItem): Observable<Boolean> {
        return Observable.fromCallable { getSchoolCode()  }
                .flatMap {
                    if (it == School.FAFU) {
                        val url = "http://jwgl.fafu.edu.cn/(${token})/${item.commentUrl}"
                        APIManager.getZhengFangAPI()
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
                    } else {
                        val url = "http://js.ifafu.cn/${item.commentUrl}"
                        APIManager.getZhengFangAPI()
                                .initParams(url, "http://js.ifafu.cn/")
                                .compose(CommentParserJS2())
                                .compose(RxUtils.ioToMain())
                                .flatMap { params ->
                                    APIManager.getZhengFangAPI()
                                            .post(url, url, params)
                                }
                                .map { true }
                    }
                }
    }

    override fun getSchoolCode(): String {
        return RepositoryImpl.getInUseUser()?.schoolCode ?: School.FAFU
    }

    override fun submit(params: MutableMap<String, String>): Observable<Boolean> {
        var user: User? = null
        return Observable.fromCallable { user = getUser() }
                .flatMap {
                    if (user!!.schoolCode == School.FAFU) {
                        val commentUrl = School.getUrl(ZhengFang.COMMENT, user)
                        val mainUrl = School.getUrl(ZhengFang.MAIN, user)
                        params["btn_tj"] = ""
                        APIManager.getZhengFangAPI()
                                .post(commentUrl, mainUrl, params)
                                .map { responseBody -> responseBody.string().contains("完成评价") }
                    } else {
                        val referer = School.getUrl(ZhengFang.MAIN, user)
                        val params2 = APIManager.getZhengFangAPI()
                                .initParams(commitUrlJS, referer)
                                .compose(CommentParserJS2())
                                .blockingFirst()
                        params2.remove("Button1")
                        params2["Button2"] = " 提  交 ".encode("gb2312")
                        APIManager.getZhengFangAPI()
                                .post(commitUrlJS, commitUrlJS, params2)
                                .map { responseBody -> responseBody.string().contains("完成评价") }
                    }
                }
    }

    override fun getJumpInfo(item: CommentItem): Map<String, String> {
        return if (getUser()!!.schoolCode == School.FAFU) {
            mapOf(
                    "title" to "评价教师【${item.teacherName}】",
                    "url" to "http://jwgl.fafu.edu.cn/(${token})/${item.commentUrl.replace("&amp;", "&")}"
            )
        } else {
            mapOf(
                    "title" to "评价课程【${item.courseName}】",
                    "url" to "http://js.ifafu.cn/${item.commentUrl}"
            )
        }
    }

}
