package cn.ifafu.ifafu.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Token {
    @PrimaryKey
    var account: String = ""
    var token: String = ""

    constructor(account: String, token: String) {
        this.account = account
        this.token = token
    }

    constructor() {
    }

}