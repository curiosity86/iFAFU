package cn.ifafu.ifafu

import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.http.parser.ScoreParser
import org.junit.Test
import java.text.ParseException
import java.text.SimpleDateFormat

class UnitTest {
    @Test
    @Throws(ParseException::class)
    fun test() {

        val i = 24;
        println("i shl 2 => " + (i shl 2))
        println("i shr 2 => " + (i shr 2))
        println("i and 7 => " + (i and 7))
        println("i or 2 => " + (i or 2))
        println("${0x100}")
    }
}