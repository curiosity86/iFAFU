package cn.woolsen.easymvvm.livedata

import androidx.lifecycle.MutableLiveData

class LiveDataString : MutableLiveData<String> {
    constructor() : super()
    constructor(value: String?) : super(value)
}