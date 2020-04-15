package cn.ifafu.ifafu.experiment.bean

/**
 * ViewModel <-> View
 */
sealed class Resource<out T> {

    data class Success<out T>(val data: T) : Resource<T>() {
        var message: String? = null
            get() {
                val message = field
                field = null
                return message
            }
    }

    data class Error(val message: String? = null) : Resource<Nothing>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=${data}]"
            is Error -> "Error[message=${message}]"
            is Loading -> "Loading[${data}]"
        }
    }

    inline fun <R> map(crossinline transform: (T?) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message)
            is Loading -> Loading(transform(data))
        }
    }
}