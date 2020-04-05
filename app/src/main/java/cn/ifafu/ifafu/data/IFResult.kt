package cn.ifafu.ifafu.data

/* 数据层统一数据返回类 */
@Suppress("UNCHECKED_CAST")
class IFResult<out T> private constructor(@PublishedApi internal val value: Any?) {

    val isSuccess: Boolean get() = value !is Failure
    val isFailure: Boolean get() = value is Failure

    fun getOrNull(): T? =
            when {
                isFailure -> null
                else -> value as T
            }

    inline fun getOrFailure(onFailure: (Exception) -> Unit): T? {
        if (value is Failure) {
            onFailure(value.exception)
            return null
        }
        return value as T
    }

    override fun toString(): String =
            when (value) {
                is Failure -> "Failure($value)"
                else -> "Success($value)"
            }

    companion object {
        private fun createException(message: String) = MessageException(message)
        fun <T> success(value: T): IFResult<T> = IFResult(value)
        fun <T> failure(exception: Exception): IFResult<T> = IFResult(Failure(exception))
        fun <T> failure(message: String): IFResult<T> = IFResult(Failure(createException(message)))
    }

    @PublishedApi
    internal class Failure(@PublishedApi internal val exception: Exception) {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = exception.toString()
    }

    internal class MessageException(message: String) : Exception(message)

}
