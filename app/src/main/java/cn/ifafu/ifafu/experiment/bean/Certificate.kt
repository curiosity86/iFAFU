package cn.ifafu.ifafu.experiment.bean

import cn.ifafu.ifafu.data.entity.User

class Certificate private constructor(
        val account: String,
        val name: String,
        val token: String
) {

    companion object {
        fun create(user: User): Certificate {
            return Certificate(user.account, user.name, user.token)
        }
    }

}