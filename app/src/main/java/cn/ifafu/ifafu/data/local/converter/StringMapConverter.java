package cn.ifafu.ifafu.data.local.converter;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Map;

public class StringMapConverter implements PropertyConverter<Map<String, Object>, String> {
    @Override
    public Map<String, Object> convertToEntityProperty(String databaseValue) {
        return JSONObject.parseObject(databaseValue).getInnerMap();
    }

    @Override
    public String convertToDatabaseValue(Map<String, Object> entityProperty) {
        return JSONObject.toJSONString(entityProperty);
    }
}
