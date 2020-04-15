package cn.ifafu.ifafu.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.ifafu.ifafu.data.entity.GlobalSetting
import cn.ifafu.ifafu.experiment.data.repository.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(private val settingRepository: SettingRepository) : ViewModel() {

    val settings: LiveData<GlobalSetting> = settingRepository.loadSetting()

    fun save(setting: GlobalSetting) {
        viewModelScope.launch(Dispatchers.IO) {
            settingRepository.saveSetting(setting)
        }
    }
}