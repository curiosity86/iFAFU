package cn.woolsen.easymvvm.livedata

import androidx.lifecycle.MutableLiveData

class LiveDataField<T> : MutableLiveData<T> {
    constructor() : super()
    constructor(value: T?) : super(value)
}