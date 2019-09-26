package cn.ifafu.ifafu.data.local.converter;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.TreeSet;

public class IntTreeSetConverter implements PropertyConverter<TreeSet<Integer>, String> {

    @Override
    public TreeSet<Integer> convertToEntityProperty(String databaseValue) {
        return new TreeSet<>(JSONObject.parseArray(databaseValue, Integer.TYPE));
    }

    @Override
    public String convertToDatabaseValue(TreeSet<Integer> entityProperty) {
        return JSONObject.toJSONString(entityProperty);
    }
}
