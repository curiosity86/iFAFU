package cn.ifafu.ifafu.ui.main.new_theme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.ifafu.ifafu.data.bean.Weather
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainNewViewModel : ViewModel() {

    val weather = MutableLiveData<Weather>()
    val user = MutableLiveData<User>()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            user.postValue(RepositoryImpl.user.getInUse())
        }
    }

    fun updateWeather() = GlobalScope.launch(Dispatchers.IO) {
        kotlin.runCatching {
            val weather = RepositoryImpl.WeatherRt.fetch("101230101").data ?: return@launch
            this@MainNewViewModel.weather.postValue(weather)
        }
    }
}