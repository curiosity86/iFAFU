package cn.ifafu.ifafu.base.mvvm

import android.app.Application
import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.entity.Response
import cn.ifafu.ifafu.entity.exception.NoAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val mRepository by lazy { Repository }

    lateinit var event: UIEvent

    /**
     * @return null 重新登录错误
     */
    protected suspend fun <T> ensureLoginStatus(callback: suspend () -> T): T? = withContext(Dispatchers.IO) {
        try {
            callback()
        } catch (e: NoAuthException) {
            val user = mRepository.getInUseUser()
                    ?: throw Resources.NotFoundException("用户信息不存在")
            val response = mRepository.login(user.account, user.password)
            when (response.code) {
                Response.FAILURE -> {
                    event.startLoginActivity()
                    null
                }
                Response.ERROR -> {
                    event.showMessage(response.message)
                    null
                }
                Response.SUCCESS -> {
                    callback()
                }
                else -> {
                    event.showMessage("未知的Response返回码")
                    null
                }
            }
        }
    }

    protected fun Throwable.errorMessage(): String {
        return when (this) {
            is UnknownHostException, is ConnectException ->
                "网络错误，请检查网络设置"
            is SocketTimeoutException ->
                "服务器连接超时（可能原因：学校服务器崩溃）"
            is SQLiteConstraintException ->
                "数据库数据错误（错误信息：${message}）"
            is IOException ->
                if (this.message?.contains("unexpected") == true) {
                    "正方教务系统又崩溃了！"
                } else {
                    message ?: "ERROR"
                }
            else ->
                message ?: "ERROR"
        }
    }

    protected suspend fun runOnMainThread(run: () -> Unit) = withContext(Dispatchers.Main) {
        run()
    }

}