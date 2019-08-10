package cn.ifafu.ifafu.data

data class Api constructor(val baseUrl: String) {

    private var name: String = ""

    private val apiMap: MutableMap<String, String> = HashMap()

    fun addApi(name: String, api: String) {
        apiMap[name] = api
    }

    operator fun get(name: String): String {
        return apiMap[name] ?: ""
    }

    override fun toString(): String {
        return "Api(baseUrl='$baseUrl', apiMap=$apiMap)"
    }
}
