package cn.ifafu.ifafu.data.http.parser

import cn.ifafu.ifafu.entity.CommentItem
import cn.ifafu.ifafu.entity.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

class CommentParserJS : BaseParser<Response<List<CommentItem>>>() {

    @Throws(Exception::class)
    override fun parse(html: String): Response<List<CommentItem>> {
        val document = Jsoup.parse(html)
        val table = document.select("li[class=\"top\"]")
        var menu: Element? = null
        for (t in table) {
            if (t.text().contains("教学质量评价")) {
                menu = t
                break
            }
        }
        if (menu == null) {
            return Response.failure("一键评教出错")
        }
        val lis = menu.getElementsByTag("a")
        if (lis.size == 1) {
            return Response.failure("无需评教")
        }
        val commentItems = ArrayList<CommentItem>()
        for (i in 1 until lis.size) {
            val li = lis[i]
            val item = CommentItem()
            item.courseName = li.text()
            item.teacherName = ""
            item.commentUrl = li.attr("href")
            commentItems.add(item)
        }
        val response = Response<List<CommentItem>>()
        response.code = Response.SUCCESS
        response.body = commentItems
        response.hiddenParams = ParamsParser().parse(html)
        return response
    }
}
