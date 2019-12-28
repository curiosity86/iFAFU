package cn.ifafu.ifafu.base.mvvm

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel() {

    protected fun Throwable.errorMessage(): String {
        return when (this) {
            is UnknownHostException, is ConnectException ->
                "网络错误，请检查网络设置"
            is SocketTimeoutException ->
                "服务器连接超时"
            is SQLiteConstraintException ->
                "数据库数据错误（错误信息：${message}）"
            else ->
                message ?: "ERROR"
        }
    }

}