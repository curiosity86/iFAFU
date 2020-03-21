package cn.ifafu.ifafu.data.repository.impl

import cn.ifafu.ifafu.data.repository.ParamsRepository

class ParamsRepositoryImpl : ParamsRepository {
    override suspend fun get(url: String): Map<String, String> {
        return emptyMap()
    }

    override suspend fun get(url: String, referer: String): Map<String, String> {
        return emptyMap()
    }

}