package cn.ifafu.ifafu.util

import java.util.regex.Pattern

fun String.getInts(): List<Int> {
    val list = ArrayList<Int>()
    val m = Pattern.compile("[0-9]+").matcher(this)
    while (m.find()) {
        list.add(m.group().toInt())
    }
    return list
}
