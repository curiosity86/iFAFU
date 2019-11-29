package cn.ifafu.ifafu.data.http.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern

import cn.ifafu.ifafu.data.entity.CommentItem
import cn.ifafu.ifafu.data.entity.Response

class CommentParserJS : BaseParser<Response<List<CommentItem>>>() {

    @Throws(Exception::class)
    override fun parse(html: String): Response<List<CommentItem>> {
        val document = Jsoup.parse(html)
        val table = document.select("li[class=\"top\"]")
        val lis = table[2].getElementsByTag("a")
        val commentItems = ArrayList<CommentItem>()
        for (li in lis) {
            val item = CommentItem()
            item.courseName = li.text()
            item.teacherName = ""
            item.commentUrl = li.attr("href")
            commentItems.add(item)
            println(li.outerHtml())
        }
        val response = Response<List<CommentItem>>()
        response.body = commentItems
        response.hiddenParams = ParamsParser().parse(html)
        return response
    }
}
