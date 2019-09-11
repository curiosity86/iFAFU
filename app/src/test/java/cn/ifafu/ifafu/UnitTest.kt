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

        val format = SimpleDateFormat("yyyy-MM-dd");

        val first = format.parse("2019-")

        val array = ArrayList<Int>()


    }
}