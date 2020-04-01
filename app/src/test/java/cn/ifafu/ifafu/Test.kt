package cn.ifafu.ifafu

import cn.ifafu.ifafu.data.bean.ElecSelection
import cn.ifafu.ifafu.data.new_http.impl.JWServiceImpl
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.*
import org.junit.Test

class Test {

    @Test
    fun testCo() {
        val json = JSONObject.toJSONString(RepositoryImpl.XfbRt.getSelectionList())

        JSONArray.parseArray(json).map {
            val json = JSONObject.parseObject(it.toString())
            ElecSelection(
                    aid = json["aid"]?.toString() ?: "",
                    name = json["name"]?.toString() ?: "",
                    areaId = json["areaId"]?.toString() ?: "",
                    area = json["area"]?.toString() ?: "",
                    buildingId = json["buildingId"]?.toString() ?: "",
                    building = json["building"]?.toString() ?: "",
                    floorId = json["floorId"]?.toString() ?: "",
                    floor = json["floor"]?.toString() ?: "",
                    group1 = json["group1"]?.toString() ?: "",
                    group2 = json["group2"]?.toString() ?: ""
            )
        }.forEach {
            println(JSONObject.toJSONString(it))
        }
    }
}