package cn.ifafu.ifafu.base

import androidx.annotation.StringRes

interface UIEvent {
    suspend fun startLoginActivity()
    suspend fun finishIt()
}