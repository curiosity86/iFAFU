package cn.ifafu.ifafu.data.newly

/**
 * HTTP 统一响应结果
 */
class WoResponse<T> {
    var code: String? = null
    var message: String? = null
    var data: T? = null
    override fun toString(): String {
        return "WebResponse(code=$code, message=$message, data=$data)"
    }


}