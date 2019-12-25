package cn.ifafu.ifafu.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ElecUser {
    @PrimaryKey
    var account: String = ""
    var xfbAccount: String= ""
    var password: String= ""
    var name: String= ""

    constructor(account: String, xfbAccount: String, password: String,
                name: String) {
        this.account = account
        this.xfbAccount = xfbAccount
        this.password = password
        this.name = name
    }

    constructor() {}

}