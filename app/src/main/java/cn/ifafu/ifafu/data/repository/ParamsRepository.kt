package cn.ifafu.ifafu.data.repository

interface ParamsRepository {
    suspend fun get(url: String): Map<String, String>
    suspend fun get(url: String, referer: String): Map<String, String>
}