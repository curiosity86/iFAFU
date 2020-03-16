package cn.ifafu.ifafu.data.retrofit.parser

import cn.ifafu.ifafu.data.entity.ElecHistory
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class ElecParser(
        private val account: String,
        private val dorm: String) {

    fun parse(info: String): ElecHistory {
        val history = ElecHistory()
        history.account = account
        history.dorm = dorm
        Pattern.compile("剩余(电量|金额):?-?[0-9]+(.[0-9]+)?").matcher(info).run {
            if (find()) {
                var balance = group().substring(5)
                if (balance.startsWith(":")) {
                    balance = balance.substring(1)
                }
                history.balance = balance.toFloat()
            } else {
                history.balance = 0F
            }
        }
        Pattern.compile("[0-9]{4}[-/][0-9]{2}[-/][0-9]{2} [0-9]{1,2}:[0-9]{2}:[0-9]{2}").matcher(info).run {
            if (find()) {
                try {
                    val time = group().replace("-", "/")
                    history.timestamp = SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.CHINA).parse(time)!!.time
                } catch (e: Exception) {
                    history.timestamp = System.currentTimeMillis()
                }
            } else {
                history.timestamp = System.currentTimeMillis()
            }
        }
        history.generateId()
        return history
    }
}