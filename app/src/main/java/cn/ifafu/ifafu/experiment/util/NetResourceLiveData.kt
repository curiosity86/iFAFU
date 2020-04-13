package cn.ifafu.ifafu.experiment.util

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.experiment.bean.Resource
import kotlinx.coroutines.Dispatchers

@Suppress("LeakingThis")
abstract class NetResourceLiveData<T> : MediatorLiveData<Resource<T>>() {

    init {
        addSource(localSource()) {
            value = Resource.Success(it)
        }
    }

    @WorkerThread
    protected abstract fun localSource(): LiveData<T>

    @WorkerThread
    protected abstract fun netSource(): Resource<T>

    fun fetchNetResource() {
        val liveData = liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            val source = netSource()
            emit(source)
        }
        addSource(liveData) {
            value = it
            if (it !is Resource.Loading) {
                removeSource(liveData)
            }
        }
    }
}