package cn.ifafu.ifafu.test

import cn.ifafu.ifafu.FileUtils
import cn.ifafu.ifafu.data.http.parser.CommentParserJS
import com.alibaba.fastjson.JSONObject
import org.junit.Test

class CommentParserJSTest {

    @Test
    @Throws(Exception::class)
    fun test() {
        val parser = CommentParserJS()
        parser.parse(FileUtils.read("$path\\002.html")).body!!.forEach {
            println(JSONObject.toJSONString(it))
        }
    }

    companion object {
        private val path = "D:\\AndroidProjects\\iFAFU\\app\\src\\test\\java\\cn\\ifafu\\ifafu\\data\\comment\\js"
    }

}
