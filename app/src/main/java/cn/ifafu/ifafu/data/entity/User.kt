package cn.ifafu.ifafu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.ifafu.ifafu.app.Constant
import java.util.*

@Entity
class User {
    @PrimaryKey
    var account: String = "" // 学号
    var password: String = "" // 密码
    var name: String = ""// 名字
    var schoolCode: String = Constant.FAFU
    var token: String = generateToken()

    private fun generateToken(): String {
        val randomStr = "abcdefghijklmnopqrstuvwxyz12345".toCharArray()
        val token = StringBuilder()
        val random = Random()
        for (i in 0..23) {
            token.append(randomStr[random.nextInt(31)])
        }
        return "($token)"
    }

}