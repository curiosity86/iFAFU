package cn.ifafu.ifafu.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.ifafu.ifafu.data.bean.SchoolCode
import java.util.*

@Entity
data class User(
        @PrimaryKey
        var account: String = "", // 学号
        var password: String = "", // 密码
        var name: String = "",// 名字
        @SchoolCode
        @ColumnInfo(name = "schoolCode")
        var school: String = FAFU,
        var token: String = "",
        @ColumnInfo(name = "last_login_time")
        var lastLoginTime: Long = System.currentTimeMillis() //上次登录时间
) {

    init {
        if (token.isEmpty()) {
            token = generateToken()
        }
    }

    private fun generateToken(): String {
        val randomStr = "abcdefghijklmnopqrstuvwxyz12345".toCharArray()
        val token = StringBuilder()
        val random = Random()
        for (i in 0..23) {
            token.append(randomStr[random.nextInt(31)])
        }
        return "($token)"
    }

    companion object {
        const val FAFU = "FAFU"
        const val FAFU_JS = "FAFU_JS"
    }

}