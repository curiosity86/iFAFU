package cn.ifafu.ifafu.data.bean

import androidx.lifecycle.MutableLiveData

class LiveEvent : MutableLiveData<Unit>() {
    fun call() {
        postValue(Unit)
    }
}