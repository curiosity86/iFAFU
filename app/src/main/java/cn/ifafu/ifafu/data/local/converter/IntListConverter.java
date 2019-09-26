package cn.ifafu.ifafu.data.local.converter;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

public class IntListConverter implements PropertyConverter<List<Integer>, String> {

    @Override
    public List<Integer> convertToEntityProperty(String databaseValue) {
        return JSONObject.parseArray(databaseValue, Integer.TYPE);
    }

    @Override
    public String convertToDatabaseValue(List<Integer> entityProperty) {
        return JSONObject.toJSONString(entityProperty);
    }
}
