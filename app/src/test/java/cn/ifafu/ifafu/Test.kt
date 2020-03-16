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
        SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.CHINA).parse("2019/01/10 1:10:10")
    }
}