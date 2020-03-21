package cn.ifafu.ifafu.jsoup

import cn.ifafu.ifafu.util.byId
import org.jsoup.Jsoup
import org.junit.Test

class DocumentKtxTest {
    @Test
    fun test() {

        val document = Jsoup.parse("")
        document.getElementById("123")
        document.byId["123"]
        document.byId["123"]
        document.byId["123"]
        document.byId["123"]
        document.byId["123"]
    }
}