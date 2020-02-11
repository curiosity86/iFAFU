package cn.ifafu.ifafu.data.network.parser

import cn.ifafu.ifafu.data.entity.CommentItem
import cn.ifafu.ifafu.data.entity.Response
import org.jsoup.Jsoup
import java.util.*
import java.util.regex.Pattern

class CommentParser : BaseParser<Response<MutableList<CommentItem>>>() {
    @Throws(Exception::class)
    override fun parse(html: String): Response<MutableList<CommentItem>> {
        val document = Jsoup.parse(html)
        val table = document.select("table[id=\"Datagrid1\"]") //定位到表格
        if (html.contains("您已经评价过！")) {
            return Response.failure("您已经评价过！")
        } else if (table.size == 0) {
            return Response.failure("评教系统暂未开放！")
        }
        val lines = table[0].getElementsByTag("tr")
        val list: MutableList<CommentItem> = ArrayList()
        for (i in 1 until lines.size) {
            val blocks = lines[i].getElementsByTag("td")
            //获取课程名字
            val courseName = blocks[0].text()
            //获取老师和评教地址
            val `as` = blocks[1].getElementsByTag("a")
            for (a in `as`) {
                val item = CommentItem()
                val urlMatcher = Pattern.compile("open\\(.*ll'").matcher(a.outerHtml())
                if (urlMatcher.find()) {
                    item.courseName = courseName
                    val aText = a.text()
                    item.isDone = aText.contains("已评价")
                    item.teacherName = aText.replace("\\(.*\\)".toRegex(), "")
                    val matchUrl = urlMatcher.group()
                            .replace("&amp;".toRegex(), "&")
                    item.commentUrl = matchUrl.substring(6, matchUrl.length - 1)
                    list.add(item)
                }
            }
        }
        Result
        return Response.success(list).apply {
            hiddenParams = ParamsParser().parse(html)
        }
    }
}