package cn.ifafu.ifafu.data.new_http

class IFResponse<T>(
        private var code: Int,
        val data: T? = null,
        val message: String? = null, //错误信息
        val params: Map<String, String>? = null
) {

    inline fun <R> getOrElse(onGet: (T) -> R, onElse: (IFResponse<T>) -> R): R {
        return if (isSuccess && data != null) {
            onGet(data)
        } else {
            onElse(this)
        }
    }

    val isSuccess: Boolean
        get() = code == SUCCESS
    val isFailure: Boolean
        get() = code == FAILURE
    val isError: Boolean
        get() = code == ERROR

    companion object {
        const val SUCCESS = 200 //成功
        const val FAILURE = 400 //失败
        const val ERROR = 500 //出错

        fun <T> success(data: T?, params: Map<String, String>? = null): IFResponse<T> {
            return IFResponse(SUCCESS, data = data, params = params)
        }

        fun <T> failure(message: String? = null): IFResponse<T> {
            return IFResponse(FAILURE, message = message)
        }

        fun <T> error(message: String? = null): IFResponse<T> {
            return IFResponse(ERROR)
        }
    }
}