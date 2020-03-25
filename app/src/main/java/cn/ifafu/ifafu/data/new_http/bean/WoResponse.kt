package cn.ifafu.ifafu.data.new_http.bean

/**
 * woolsen.cn 返回类
 */
class WoResponse<T> {
    var code: String? = null
    var message: String? = null
    var data: T? = null
    
    override fun toString(): String {
        return "WebResponse(code=$code, message=$message, data=$data)"
    }
}