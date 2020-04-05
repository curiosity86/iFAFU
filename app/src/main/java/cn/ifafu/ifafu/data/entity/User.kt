package cn.ifafu.ifafu.data.entity

import androidx.annotation.StringDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class User {
    @PrimaryKey
    var account: String = "" // 学号
    var password: String = "" // 密码
    var name: String = ""// 名字
    @School
    @ColumnInfo(name = "schoolCode")
    var school: String = FAFU
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

    companion object {
        const val FAFU = "FAFU"
        const val FAFU_JS = "FAFU_JS"
    }

    @MustBeDocumented
    @StringDef(value = [FAFU, FAFU_JS])
    annotation class School
}