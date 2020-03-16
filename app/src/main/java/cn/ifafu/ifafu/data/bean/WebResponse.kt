package cn.ifafu.ifafu.data.bean

/**
 * HTTP 统一响应结果
 */
data class WebResponse<T>(
     val code: Int = 0,
     val message: String = "",
     val data: T? = null
)