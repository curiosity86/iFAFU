package cn.woolsen.easymvvm.livedata

import androidx.lifecycle.MutableLiveData

class LiveDataBoolean : MutableLiveData<Boolean> {
    constructor() : super()
    constructor(value: Boolean?) : super(value)
}