package cn.ifafu.ifafu.base

import android.app.Application
import android.content.Intent
import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.bean.Response
import cn.ifafu.ifafu.data.exception.NoAuthException
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.ui.login.LoginActivity
import kotlinx.coroutines.*
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.security.auth.login.LoginException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    var event: UIEvent? = null

    /**
     * 登录态失效时自动重新登录，成功后重新执行代码块，否则提示错误
     * @return null 重新登录错误
     */
    protected suspend fun <T> ensureLoginStatus(block: suspend () -> T): T {
        try {
            return block()
        } catch (e: NoAuthException) {
            val user = RepositoryImpl.user.getInUse()
                    ?: throw Resources.NotFoundException("用户信息不存在")
            return RepositoryImpl.user.login(user).run {
                when (code) {
                    Response.FAILURE -> {
                        event?.startLoginActivity()
                        throw LoginException(message)
                    }
                    Response.ERROR -> {
                        throw LoginException(message)
                    }
                    Response.SUCCESS -> {
                        block()
                    }
                    else -> {
                        throw UnknownError()
                    }
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
                    message ?: "Net Error"
                }
            is LoginException -> {
                val app = getApplication<IFAFU>()
                app.startActivity(Intent(app, LoginActivity::class.java))
                message ?: "登录出错"
            }
            else ->
                message ?: "ERROR"
        }
    }

    protected suspend fun runOnMainThread(run: () -> Unit) = withContext(Dispatchers.Main) {
        run()
    }

    protected fun safeLaunchWithMessage(
            context: CoroutineContext = EmptyCoroutineContext,
            start: CoroutineStart = CoroutineStart.DEFAULT,
            block: suspend CoroutineScope.() -> Unit): Job {
        return safeLaunch(context, start, block) {
            toast(it.errorMessage())
        }
    }

    protected fun safeLaunch(
            context: CoroutineContext = EmptyCoroutineContext,
            start: CoroutineStart = CoroutineStart.DEFAULT,
            block: suspend CoroutineScope.() -> Unit,
            error: suspend (Exception) -> Unit): Job {
        return GlobalScope.launch(context, start) {
            try {
                block()
            } catch (e: Exception) {
                e.printStackTrace()
                error(e)
            }
        }
    }

    protected suspend fun toast(message: String) = withContext(Dispatchers.Main) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }

    protected suspend fun toast(@StringRes resId: Int) = withContext(Dispatchers.Main) {
        Toast.makeText(getApplication(), resId, Toast.LENGTH_SHORT).show()
    }
}