package cn.ifafu.ifafu.base

import androidx.annotation.StringRes

interface UIEvent {
    suspend fun showMessage(message: String)
    suspend fun showMessage(@StringRes msgId: Int)
    suspend fun showDialog()
    suspend fun hideDialog()
    suspend fun startLoginActivity()
    suspend fun finishIt()
}