package cn.ifafu.ifafu.experiment.util

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.experiment.bean.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class NetBoundResource<T> @MainThread constructor(
        private val coroutineScope: CoroutineScope) {

    private val result = MediatorLiveData<Resource<T>>()

    init {
        result.value = Resource.Loading()
        @Suppress("LeakingThis")
        val dbResource = loadFromDb()
        result.addSource(dbResource) { data ->
            result.removeSource(dbResource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbResource)
            } else {
                result.addSource(dbResource) { newData ->
                    setValue(Resource.Success(newData))
                }
            }
        }
    }

    private fun setValue(value: Resource<T>) {
        if (result.value !== value) {
            result.value = value
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<T>) {
        //预加载本地数据
        result.addSource(dbSource) {
            setValue(Resource.Loading(it))
        }
        //先取消订阅，防止数据库刷新数据同步导致Loading被取消
        result.removeSource(dbSource)
        coroutineScope.launch(Dispatchers.IO) {
            when (val response = createCall()) {
                is IFResponse.Success -> {
                    saveCallResult(response.data)
                    //重新订阅数据库数据
                    withContext(Dispatchers.Main) {
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.Success(newData))
                        }
                    }
                }
                is IFResponse.Failure -> {
                    withContext(Dispatchers.Main) {
                        setValue(response.asResource())
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.Success(newData))
                        }
                    }
                }
                is IFResponse.Error -> {
                    withContext(Dispatchers.Main) {
                        onFetchError(response.exception)
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.Success(newData))
                        }
                    }
                }
            }
        }
    }

    protected open fun onFetchError(exception: Exception) {}

    @MainThread
    protected abstract fun loadFromDb(): LiveData<T>

    @MainThread
    protected abstract fun shouldFetch(data: T?): Boolean

    @MainThread
    protected abstract fun createCall(): IFResponse<T>

    @WorkerThread
    protected abstract fun saveCallResult(item: T)

    fun asLiveData() = result as LiveData<Resource<T>>
}