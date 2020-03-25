package cn.woolsen.easymvvm.livedata

import androidx.lifecycle.MutableLiveData

class LiveDataInt : MutableLiveData<Int> {
    constructor() : super()
    constructor(value: Int?) : super(value)
}