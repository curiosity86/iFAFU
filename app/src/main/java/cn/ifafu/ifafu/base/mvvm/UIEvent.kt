package cn.ifafu.ifafu.base.mvvm

interface UIEvent {
    suspend fun showMessage(message: String)
    suspend fun showDialog()
    suspend fun hideDialog()
    suspend fun startLoginActivity()
    suspend fun finishIt()
}