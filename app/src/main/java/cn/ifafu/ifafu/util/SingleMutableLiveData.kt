package cn.ifafu.ifafu.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * 每次set value只被观察一次
 */
class SingleMutableLiveData<T> : MutableLiveData<T>() {

    override fun postValue(value: T) {
        super.postValue(value)
        super.postValue(null)
    }

    override fun setValue(value: T) {
        super.setValue(value)
        super.setValue(null)
    }

}