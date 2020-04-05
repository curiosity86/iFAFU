package cn.ifafu.ifafu.experiment.vo


data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?, msg: String? = null): Resource<T>
            = Resource(Status.SUCCESS, data, msg)

        fun <T> error(msg: String, data: T?): Resource<T>
            = Resource(Status.ERROR, data, msg)

        fun <T> loading(data: T?): Resource<T>
            = Resource(Status.LOADING, data, null)

    }
}
