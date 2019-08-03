package cn.woolsen.android.uitl;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.collection.SimpleArrayMap;

import java.util.Set;

import cn.woolsen.android.mvp.BaseApplication;

/**
 * create by woolsen on 19/7/12
 */
public class SPUtils {

    private SharedPreferences sp;

    private static SimpleArrayMap<String, SPUtils> map = new SimpleArrayMap<>();

    public static SPUtils get(String fileName) {
        if (!map.containsKey(fileName)) {
            map.put(fileName, new SPUtils(BaseApplication.getContext(), fileName));
        }
        return map.get(fileName);
    }

    private SPUtils(Context context, String fileName) {
        sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
    
    public void putString(String key, String value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public void putStringSet(String key, Set<String> value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putStringSet(key, value);
        edit.apply();
    }

    public void putInt(String key, Integer value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        return sp.getStringSet(key, defaultValue);
    }

    public String getString(String key) {
        return sp.getString(key, "");
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    public boolean contain(String key) {
        return sp.contains(key);
    }

    public void clear() {
        sp.edit().clear().apply();
    }
}
