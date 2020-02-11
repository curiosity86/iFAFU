package cn.ifafu.ifafu.data.entity

data class Response<T>(
        val code: Int,
        val message: String = "",
        val data: T? = null,
        var hiddenParams: MutableMap<String, String>? = null
) {
    val isSuccess: Boolean
        get() = code == SUCCESS
    companion object {
        /**
         * 成功
         */
        const val SUCCESS = 0
        /**
         * 部分失败
         */
        const val FAILURE = 1
        /**
         * 出错
         */
        const val ERROR = 2

        fun <T> success(data: T): Response<T> = Response(code = SUCCESS, data = data)
        fun <T> failure(msg: String): Response<T> = Response(code = FAILURE, message = msg)
        fun <T> failure(data: T, msg: String): Response<T> = Response(code = FAILURE, data = data, message = msg)
        fun <T> error(msg: String): Response<T> = Response(code = ERROR, message = msg)
    }
}