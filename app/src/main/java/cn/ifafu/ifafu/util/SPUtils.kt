package cn.ifafu.ifafu.util

import android.content.Context
import android.content.SharedPreferences
import androidx.collection.SimpleArrayMap
import androidx.core.content.edit
import cn.ifafu.ifafu.base.BaseApplication

/**
 * create by woolsen on 19/7/12
 */
class SPUtils private constructor(context: Context, fileName: String) {

    private val sp: SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun putString(key: String, value: String) {
        sp.edit { putString(key, value) }
    }

    fun putStringSet(key: String, value: Set<String>) {
        sp.edit { putStringSet(key, value) }
    }

    fun putInt(key: String, value: Int) {
        sp.edit { putInt(key, value) }
    }

    fun putBoolean(key: String, value: Boolean) {
        sp.edit { putBoolean(key, value) }
    }

    fun getStringSet(key: String): Set<String> {
        return sp.getStringSet(key, emptySet())!!
    }

    fun getString(key: String): String {
        return sp.getString(key, "")!!
    }

    fun getBoolean(key: String): Boolean {
        return sp.getBoolean(key, false)
    }

    val all: MutableMap<String, *>
        get() = sp.all

    fun remove(key: String) {
        sp.edit {
            remove(key)
        }
    }

    fun contain(key: String): Boolean {
        return sp.contains(key)
    }

    fun clear() {
        sp.edit().clear().apply()
    }

    companion object {
        private val map = SimpleArrayMap<String, SPUtils>()
        @JvmStatic
        operator fun get(fileName: String): SPUtils {
            if (!map.containsKey(fileName)) {
                map.put(fileName, SPUtils(BaseApplication.appContext, fileName))
            }
            return map[fileName]!!
        }
    }

}