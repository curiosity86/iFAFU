package cn.ifafu.ifafu.ui.setting

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.data.entity.GlobalSetting
import cn.ifafu.ifafu.ui.view.adapter.syllabus_setting.CheckBoxItem
import cn.ifafu.ifafu.ui.view.adapter.syllabus_setting.SettingItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingViewModel(application: Application) : BaseViewModel(application) {

    private var originalTheme = -1
    val settings by lazy { MutableLiveData<List<SettingItem>>() }
    val needCheckTheme by lazy { MutableLiveData<Boolean>() }

    private lateinit var setting: GlobalSetting

    fun initSetting() {
        safeLaunchWithMessage {
            setting = RepositoryImpl.GlobalSettingRt.get()
            originalTheme = setting.theme
            settings.postValue(listOf(CheckBoxItem("旧版主页主题", "应需求而来，喜欢0.9版本iFAFU界面就快来呀", setting.theme == GlobalSetting.THEME_OLD) {
                setting.theme = if (it) GlobalSetting.THEME_OLD else GlobalSetting.THEME_NEW
                needCheckTheme.postValue(originalTheme != setting.theme)
            }))
        }
    }

    fun save() {
        GlobalScope.launch {
            RepositoryImpl.GlobalSettingRt.save(setting)
        }
    }
}