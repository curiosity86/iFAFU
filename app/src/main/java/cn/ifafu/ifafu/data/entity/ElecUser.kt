package cn.ifafu.ifafu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ElecUser {
    @PrimaryKey
    var account: String = ""
    var xfbAccount: String= ""
    var xfbId: String = ""
    var password: String= ""

}