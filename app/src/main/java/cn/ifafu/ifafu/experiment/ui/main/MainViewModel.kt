package cn.ifafu.ifafu.experiment.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import cn.ifafu.ifafu.experiment.data.repository.SettingRepository

class MainViewModel(private val settingRepository: SettingRepository) : ViewModel() {
    val theme = settingRepository.loadSetting().map { it.theme }
}