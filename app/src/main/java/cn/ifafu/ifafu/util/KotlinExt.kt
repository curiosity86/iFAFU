package cn.ifafu.ifafu.util

import java.net.URLEncoder
import java.util.regex.Pattern

fun String.getInts(): List<Int> {
    val list = ArrayList<Int>()
    val m = Pattern.compile("[0-9]+").matcher(this)
    while (m.find()) {
        list.add(m.group().toInt())
    }
    return list
}

fun String.encode(enc: String): String {
    return URLEncoder.encode(this, enc)
}

/**
 * Returns the sum of all values produced by [selector] function applied to each element in the collection.
 */
inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0F
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
