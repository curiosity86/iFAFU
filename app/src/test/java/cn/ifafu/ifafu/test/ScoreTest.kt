package cn.ifafu.ifafu.test

import cn.ifafu.ifafu.FileUtils
import cn.ifafu.ifafu.data.network.parser.ScoreParser
import cn.ifafu.ifafu.data.entity.User

class ScoreTest {
    companion object {

        private val path = "C:\\Users\\WengK\\Desktop\\all_score.html"

        @JvmStatic
        fun main(args: Array<String>) {
            val html = FileUtils.read(path, "utf-8")
            ScoreParser(User()).parse(html).data!!.forEach {
                if (it.attr.isNotBlank())
                println("${it.name}   ${it.nature}   ${it.attr}   ${it.credit}   ${it.score}")
            }
        }
    }
}