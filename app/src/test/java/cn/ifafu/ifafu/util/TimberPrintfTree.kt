package cn.ifafu.ifafu.util

import timber.log.Timber

class TimberPrintfTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        println("${tag ?: ""}#${message}")
        if (t != null) {
            println("${tag ?: ""}#${t}")
        }
    }
}