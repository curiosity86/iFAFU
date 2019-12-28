package cn.ifafu.ifafu.data.http

import android.graphics.Bitmap
import cn.ifafu.ifafu.entity.Electives
import cn.ifafu.ifafu.entity.Score

interface HttpDataSource {
    //获取隐藏参数
    suspend fun fetchParams(url: String, referer: String): MutableMap<String, String>
    //选修学分要求查询
    suspend fun fetchElectives(): Electives

    //成绩查询
    suspend fun fetchScoreList(): List<Score>
    suspend fun fetchScoreList(year: String, term: String): List<Score>

    //电费查询
    suspend fun elecLoginInit()
    suspend fun elecLogin(account: String, password: String, verify: String): String
    suspend fun elecVerifyBitmap(): Bitmap
}