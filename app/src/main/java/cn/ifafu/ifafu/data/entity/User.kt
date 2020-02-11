package cn.ifafu.ifafu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.ifafu.ifafu.app.School
import java.util.*

@Entity
class User {
    @PrimaryKey
    var account // 学号
            : String = ""
    var password // 密码
            : String = ""
    var name // 名字
            : String = ""
    /**
     * [School.FAFU]
     * [School.FAFU_JS]
     */
    var schoolCode = School.FAFU

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