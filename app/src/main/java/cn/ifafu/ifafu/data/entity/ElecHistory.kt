package cn.ifafu.ifafu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ElecHistory {
    @PrimaryKey
    var id: Long = 0L // 等于(timestamp + dorm + account).hashCode()
    var dorm: String = "" //宿舍号
    var balance: Float = 0F
    var timestamp: Long = 0L
    var account: String = ""

    fun generateId() {
        this.id = "$timestamp$dorm$account".hashCode().toLong()
    }
}