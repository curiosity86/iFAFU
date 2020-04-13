package cn.ifafu.ifafu.experiment.bean

import java.io.IOException

/**
 * Repository <-> Service
 */
sealed class IFResponse<out T> {
    data class Success<out T>(val data: T) : IFResponse<T>()
    data class Error(val exception: Exception) : IFResponse<Nothing>()
    data class Failure(val message: String) : IFResponse<Nothing>()
    object NoAuth : IFResponse<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=${data}]"
            is Error -> "Error[exception=${exception}]"
            is Failure -> "Failure[message=${message}]"
            is NoAuth -> "NoAuth"
        }
    }

    fun asResource(): Resource<T> {
        return when (this) {
            is Success -> Resource.Success(data)
            is Error -> Resource.Error(exception.errorMessage())
            is Failure -> Resource.Error(message)
            is NoAuth -> Resource.Error("登录态失效")
        }
    }

    private fun Exception.errorMessage(): String {
        return when (this) {
            is IOException -> "网络异常，请检查网络设置"
            else -> "Error:${toString()}"
        }
    }
}
