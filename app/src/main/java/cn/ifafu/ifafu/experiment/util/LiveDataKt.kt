package cn.ifafu.ifafu.experiment.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.util.Event

inline fun <X, Y> LiveData<Resource<X>>.successMap(crossinline transform: (X) -> Y): LiveData<Y> {
    val result = MediatorLiveData<Y>()
    result.addSource(this) {
        if (it is Resource.Success && it.data != null) {
            result.value = transform(it.data)
        }
    }
    return result
}

fun <X> LiveData<X>.toMediatorLiveData(): MediatorLiveData<X> {
    if (this is MediatorLiveData) {
        return this
    }
    val liveData = MediatorLiveData<X>()
    liveData.addSource(this) {
        liveData.value = it
    }
    return liveData
}

fun <X> LiveData<X>.toEventLiveData(): LiveData<Event<X>> {
    return this.map { Event((it)) }
}