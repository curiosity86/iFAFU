package cn.ifafu.ifafu.experiment.db

import cn.ifafu.ifafu.data.entity.User

interface ZFService {
    fun login(user: User)

    fun fetchScores(user: User)
}