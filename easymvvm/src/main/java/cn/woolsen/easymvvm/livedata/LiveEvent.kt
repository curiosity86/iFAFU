package cn.woolsen.easymvvm.livedata

import androidx.lifecycle.MutableLiveData

class LiveEvent : MutableLiveData<Unit>() {
    fun call() {
        postValue(Unit)
    }
}