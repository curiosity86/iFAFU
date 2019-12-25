package cn.ifafu.ifafu.data.local.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson.JSONObject

class StringMapConverter {
    @TypeConverter
    fun convertToEntityProperty(databaseValue: String): HashMap<String, String> {
        return HashMap(JSONObject.parseObject(databaseValue).innerMap as Map<String, String>)
    }

    @TypeConverter
    fun convertToDatabaseValue(entityProperty: HashMap<String, String>): String {
        return JSONObject.toJSONString(entityProperty)
    }
}