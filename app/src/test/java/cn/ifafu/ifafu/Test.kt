package cn.ifafu.ifafu

import cn.ifafu.ifafu.util.HttpUtils
import cn.ifafu.ifafu.util.encode
import com.alibaba.fastjson.JSONObject
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class Test {
    @Test
    fun test() {
        val openingDay = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse("2020-03-16")!!.time
        for (i in 16..30) {
            val now = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse("2020-03-${i}")!!.time
            println("2020-03-${i}    " + ((now - openingDay) / 1000 / 60 / 60 / 24 / 7 + 1))
        }
    }
}