//package cn.ifafu.ifafu.ui.comment
//
//import android.content.Context
//import cn.ifafu.ifafu.constant.School
//import cn.ifafu.ifafu.base.mvp.BaseModel
//import cn.ifafu.ifafu.data.repository.Repository
//import cn.ifafu.ifafu.data.retrofit.APIManager
//import cn.ifafu.ifafu.data.retrofit.parser.CommentParser
//import cn.ifafu.ifafu.data.retrofit.parser.CommentParser2
//import cn.ifafu.ifafu.data.retrofit.parser.CommentParserJS
//import cn.ifafu.ifafu.data.retrofit.parser.CommentParserJS2
//import cn.ifafu.ifafu.data.entity.CommentItem
//import cn.ifafu.ifafu.data.entity.Response
//import cn.ifafu.ifafu.data.entity.User
//import cn.ifafu.ifafu.data.entity.ZFApiList
//import cn.ifafu.ifafu.util.RxUtils
//import cn.ifafu.ifafu.util.encode
//import io.reactivex.Observable
//import java.net.URLDecoder
//
//class CommentModel(context: Context) : BaseModel(context), CommentContract.Model {
//
//    private val token: String
//        get() = Repository.getToken(getUser()!!.account)!!.token
//
//    private var commitUrlJS = ""
//
//    override fun getCommentList(): Observable<Response<MutableList<CommentItem>>> {
//        var user: User? = null
//        var mainUrl = ""
//        var commentUrl = ""
//        return Observable.fromCallable {
//            user = getUser()
//            mainUrl = School.getUrl(ZFApiList.MAIN, user!!)
//            commentUrl = if (user!!.schoolCode == School.FAFU_JS) {
//                mainUrl
//            } else {
//                School.getUrl(ZFApiList.COMMENT, user!!)
//            }
//        }
//                .flatMap { initParams(commentUrl, mainUrl) }
//                .flatMap { _ ->
//                    APIManager.zhengFangAPI
//                            .getInfo(commentUrl, mainUrl)
//                }
//                .flatMap {
//                    if (user!!.schoolCode == School.FAFU_JS) {
//                        Observable.fromCallable { it }.compose(CommentParserJS())
//                                .map { response ->
//                                    if (!response.isSuccess) {
//                                        throw Exception(response.message)
//                                    }
//                                    val item = response.data!!.firstOrNull()
//                                    if (item != null) {
//                                        val referer = "http://js.ifafu.cn/"
//                                        val url = "http://js.ifafu.cn/" + item.commentUrl
//                                        commitUrlJS = url
//                                        println("url = $url")
//                                        val done = APIManager.zhengFangAPI
//                                                .initParams(url, referer)
//                                                .map {
//                                                    val html = it.string()
//                                                    html.contains("所有评价已完成")
//                                                }.blockingFirst()
//                                        if (done) {
//                                            response.data!!.forEach {
//                                                it.isDone = done
//                                            }
//                                        }
//                                    }
//                                    response
//                                }
//                    } else {
//                        Observable.fromCallable { it }.compose(CommentParser())
//                    }
//                }
//    }
//
//    override fun commentTeacher(item: CommentItem): Observable<Boolean> {
//        return Observable.fromCallable { getSchoolCode()  }
//                .flatMap {
//                    if (it == School.FAFU) {
//                        val url = "http://jwgl.fafu.edu.cn/(${token})/${item.commentUrl}"
//                        APIManager.zhengFangAPI
//                                .initParams(url)
//                                .compose(CommentParser2())
//                                .compose(RxUtils.ioToMain())
//                                .flatMap { params ->
//                                    params["txt_pjxx"] = ""
//                                    params["Button1"] = URLDecoder.decode("+%CC%E1+%BD%BB+", "GBK")
//                                    APIManager.zhengFangAPI
//                                            .post(url, params)
//                                }
//                                .map { true }
//                    } else {
//                        val url = "http://js.ifafu.cn/${item.commentUrl}"
//                        APIManager.zhengFangAPI
//                                .initParams(url, "http://js.ifafu.cn/")
//                                .compose(CommentParserJS2())
//                                .compose(RxUtils.ioToMain())
//                                .flatMap { params ->
//                                    APIManager.zhengFangAPI
//                                            .post(url, url, params)
//                                }
//                                .map { true }
//                    }
//                }
//    }
//
//    override fun getSchoolCode(): String {
//        return Repository.getInUseUser()?.schoolCode ?: School.FAFU
//    }
//
//    override fun submit(params: MutableMap<String, String>): Observable<Boolean> {
//        return Observable.fromCallable { getUser() }
//                .flatMap { user ->
//                    if (user.schoolCode == School.FAFU) {
//                        val commentUrl = School.getUrl(ZFApiList.COMMENT, user)
//                        val mainUrl = School.getUrl(ZFApiList.MAIN, user)
//                        params["btn_tj"] = ""
//                        APIManager.zhengFangAPI
//                                .post(commentUrl, mainUrl, params)
//                                .map { responseBody -> responseBody.string().contains("完成评价") }
//                    } else {
//                        val referer = School.getUrl(ZFApiList.MAIN, user)
//                        val params2 = APIManager.zhengFangAPI
//                                .initParams(commitUrlJS, referer)
//                                .compose(CommentParserJS2())
//                                .blockingFirst()
//                        params2.remove("Button1")
//                        params2["Button2"] = " 提  交 ".encode("gb2312")
//                        APIManager.zhengFangAPI
//                                .post(commitUrlJS, commitUrlJS, params2)
//                                .map { responseBody -> responseBody.string().contains("完成评价") }
//                    }
//                }
//    }
//
//    override fun getJumpInfo(item: CommentItem): Map<String, String> {
//        return if (getUser()!!.schoolCode == School.FAFU) {
//            mapOf(
//                    "title" to "评价教师【${item.teacherName}】",
//                    "url" to "http://jwgl.fafu.edu.cn/(${token})/${item.commentUrl.replace("&amp;", "&")}"
//            )
//        } else {
//            mapOf(
//                    "title" to "评价课程【${item.courseName}】",
//                    "url" to "http://js.ifafu.cn/${item.commentUrl}"
//            )
//        }
//    }
//
//}
