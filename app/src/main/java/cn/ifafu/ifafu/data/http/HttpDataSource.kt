package cn.ifafu.ifafu.data.http

import android.graphics.Bitmap
import cn.ifafu.ifafu.entity.ElecQuery
import cn.ifafu.ifafu.entity.Electives
import cn.ifafu.ifafu.entity.Response
import cn.ifafu.ifafu.entity.Score

interface HttpDataSource {

    /**
     * 登录
     * @return [Response.SUCCESS] 登录成功 body = Name
     * [Response.FAILURE] 信息错误 msg = return msg
     * [Response.ERROR]   服务器错误  msg = error msg
     */
    suspend fun login(account: String, password: String): Response<String>

    //获取隐藏参数
    suspend fun fetchParams(url: String): MutableMap<String, String>
    suspend fun fetchParams(url: String, referer: String): MutableMap<String, String>

    //选修学分要求查询
    suspend fun fetchElectives(): Electives

    //成绩查询
    suspend fun fetchScoreList(): List<Score>

    suspend fun fetchScoreList(year: String, term: String): List<Score>

    //电费查询
    suspend fun elecCookieInit()
    suspend fun elecLogin(account: String, password: String, verify: String): String
    suspend fun elecVerifyBitmap(): Bitmap
    suspend fun checkLoginStatus(): Boolean
    suspend fun elecCardBalance(): Response<Double>
    suspend fun fetchElectricityInfo(query: ElecQuery): Response<String>
}