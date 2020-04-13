package cn.ifafu.ifafu.experiment.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import cn.ifafu.ifafu.experiment.bean.IFResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NetBoundResource2<T> {

    private lateinit var scope: CoroutineScope
    private lateinit var loadFromDb: (() -> LiveData<T>)
    private lateinit var loadFromNetwork: (() -> IFResponse<T>)
    private lateinit var saveCallResult: ((T) -> Unit)

    private var force = false

    fun coroutineScope(scope: CoroutineScope) = apply { this.scope = scope }
    fun forceRefresh(force: Boolean) = apply { this.force = force }
    fun loadFromNetwork(loadFromNetwork: () -> IFResponse<T>) = apply { this.loadFromNetwork = loadFromNetwork }
    fun setLoadFromDb(loadFromDb: () -> LiveData<T>) = apply { this.loadFromDb = loadFromDb }
    fun setSaveCallResult(saveCallResult: ((T) -> Unit)) = apply { this.saveCallResult = saveCallResult }

    fun asLiveData(): MediatorLiveData<T> {
        return MediatorLiveData<T>().apply {
            scope.launch {
                if (force) {
                    when (val response = loadFromNetwork.invoke()) {
                        is IFResponse.Success -> {
                            saveCallResult.invoke(response.data)

//                    saveCallResult(response.data)
//                    //重新订阅数据库数据
//                    withContext(Dispatchers.Main) {
//                        result.addSource(loadFromDb()) { newData ->
//                            setValue(Resource.Success(newData))
//                        }
//                    }
                        }
                        is IFResponse.Failure -> {
//                    withContext(Dispatchers.Main) {
//                        setValue(response.asResource())
//                        result.addSource(loadFromDb()) { newData ->
//                            setValue(Resource.Success(newData))
//                        }
//                    }
                        }
                        is IFResponse.Error -> {
//                    withContext(Dispatchers.Main) {
//                        onFetchError(response.exception)
//                        result.addSource(loadFromDb()) { newData ->
//                            setValue(Resource.Success(newData))
//                        }
//                    }
                        }
                    }
                }

            }
        }

    }

}