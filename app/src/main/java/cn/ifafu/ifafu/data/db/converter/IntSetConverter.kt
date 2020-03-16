package cn.ifafu.ifafu.data.db.converter

import androidx.room.TypeConverter
import com.alibaba.fastjson.JSONObject

class LongHashSetConverter {
    @TypeConverter
    fun convertToEntityProperty(databaseValue: String): HashSet<Long> {
        return JSONObject.parseArray(databaseValue, Long::class.java).toHashSet()
    }

    @TypeConverter
    fun convertToDatabaseValue(entityProperty: HashSet<Long>): String {
        return JSONObject.toJSONString(entityProperty)
    }

}