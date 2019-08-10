package cn.ifafu.ifafu.data

import cn.ifafu.ifafu.data.announce.HttpCode

class Response<T>(
        val code: Int,
        val body: T? = null,
        val message: String = "",
        val viewState: String? = null,
        val viewStateGenerator: String ? = null
) {

    val isSuccess: Boolean
        get() = this.code == SUCCESS

    companion object {

        const val SUCCESS = 200
        const val FAILURE = 400
        const val ERROR = 500

        fun <T> success(body: T): Response<T> {
            return Response(SUCCESS, body)
        }

        fun <T> success(body: T, msg: String): Response<T> {
            return Response(SUCCESS, body, msg)
        }

        fun <T> success(msg: String, body: T, viewState: String, viewStateGenerator: String): Response<T> {
            return Response(SUCCESS, body, msg, viewState, viewStateGenerator)
        }

        fun <T> failure(msg: String): Response<T> {
            return Response(FAILURE, message = msg)
        }

        fun <T> failure(msg: String, body: T): Response<T> {
            return Response(FAILURE, body, msg)
        }

        fun <T> error(msg: String): Response<T> {
            return Response(ERROR, message = msg)
        }
    }

}
