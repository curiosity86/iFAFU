package cn.ifafu.ifafu.experiment.bean

/**
 * ViewModel <-> View
 */
sealed class Resource<out T> {

    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String? = null) : Resource<Nothing>()
    data class Loading<out T>(val data: T? = null) : Resource<T>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=${data}]"
            is Error -> "Error[message=${message}]"
            is Loading -> "Loading[${data}]"
        }
    }

    inline fun <Result> map(crossinline transform: (T?) -> Result): Resource<Result> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message)
            is Loading -> Loading(transform(data))
        }
    }
}