package cn.ifafu.ifafu.entity

class Response<T> {
    var code = 0

    var message: String = ""
        private set

    var body: T? = null

    var hiddenParams: MutableMap<String, String>? = null

    constructor() {}

    constructor(code: Int, message: String = "") {
        this.code = code
        this.message = message
        this.body = body
    }

    constructor(code: Int, body: T, message: String = "") {
        this.code = code
        this.message = message
        this.body = body
    }

    val isSuccess: Boolean
        get() = code == SUCCESS

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

        fun <T> failure(msg: String): Response<T> {
            return Response(FAILURE, msg)
        }

        fun <T> failure(body: T, msg: String): Response<T> {
            return Response(FAILURE, body, msg)
        }

        fun error(msg: String): Response<String> {
            return Response(ERROR, msg, msg)
        }
    }
}