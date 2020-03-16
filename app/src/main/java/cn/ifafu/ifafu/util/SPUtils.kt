package cn.ifafu.ifafu.util

import android.content.Context
import android.content.SharedPreferences
import androidx.collection.SimpleArrayMap
import cn.ifafu.ifafu.base.BaseApplication

/**
 * SharedPreferences工具
 *
 * create by woolsen on 19/7/12
 */
class SPUtils private constructor(context: Context, fileName: String) {

    private val sp: SharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun putString(key: String, value: String) {
        sp.edit().apply {
            putString(key, value)
        }.apply()
    }

    fun putStringSet(key: String, value: Set<String>) {
        sp.edit().apply {
            putStringSet(key, value)
        }.apply()
    }

    fun putInt(key: String, value: Int) {
        sp.edit().apply {
            putInt(key, value)
        }.apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        sp.edit().apply { putBoolean(key, value)
        }.apply()
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
        sp.edit().apply {
            remove(key)
        }.apply()
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
            return map[fileName] ?: kotlin.run {
                val sp = SPUtils(BaseApplication.appContext, fileName)
                map.put(fileName, sp)
                sp
            }
        }
    }

}