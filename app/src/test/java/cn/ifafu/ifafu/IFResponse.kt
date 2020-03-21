package cn.ifafu.ifafu

import com.alibaba.fastjson.annotation.JSONCreator

class IFResponse<T> {
    var code: Int? = 0
    var data: T? = null
    var message: String? = null //错误信息
    var params: Map<String, String>? = null

    @JSONCreator
    constructor()
    constructor(code: Int, data: T? = null, message: String? = null, params: Map<String, String>? = null) {
        this.code = code
        this.data = data
        this.message = message
        this.params = params
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