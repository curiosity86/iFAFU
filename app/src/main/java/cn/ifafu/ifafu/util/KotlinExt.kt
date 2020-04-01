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

/**
 * @param enc gb2312
 */
fun String.encode(enc: String = "gb2312"): String {
    return URLEncoder.encode(this, enc)
}

inline fun Boolean.ifTrue(run: () -> Unit) {
    if (this) {
        run()
    }
}

inline fun Boolean.ifFalse(run: () -> Unit) {
    if (!this) {
        run()
    }
}

/**
 * Returns the float String without the end 0
 */
fun Float.trimEnd(radius: Int = 99): String {
    var num = if (radius == 99) toString() else toString(radius)
    if (num.indexOf(".") > 0) { // 去掉多余的0
        num = num.replace("0+$".toRegex(), "")
        // 如最后一位是.则去掉
        num = num.replace("[.]$".toRegex(), "")
    }
    return num
}

/**
 * Returns the double String without the end 0
 */
fun Double.trimEnd(radius: Int = 99): String {
    var num = if (radius == 99) toString() else toString(radius)
    if (num.indexOf(".") > 0) { // 去掉多余的0
        num = num.replace("0+$".toRegex(), "")
        // 如最后一位是.则去掉
        num = num.replace("[.]$".toRegex(), "")
    }
    return num
}

fun Float.toString(radius: Int): String {
    return String.format("%.${radius}f", this)
}

fun Double.toString(radius: Int): String {
    return String.format("%.${radius}f", this)
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
