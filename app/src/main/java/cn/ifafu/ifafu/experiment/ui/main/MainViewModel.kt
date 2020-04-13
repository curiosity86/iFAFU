package cn.ifafu.ifafu.experiment.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val theme = MutableLiveData<Int>()
}