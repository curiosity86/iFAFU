package cn.ifafu.ifafu.mvp.setting

import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.local.LocalDataSource
import cn.ifafu.ifafu.entity.GlobalSetting
import cn.ifafu.ifafu.view.adapter.syllabus_setting.CheckBoxItem
import cn.ifafu.ifafu.view.adapter.syllabus_setting.SettingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: LocalDataSource) : BaseViewModel() {

    private var isNeedCheckTheme = false

    fun initSetting(success: suspend (List<SettingItem>) -> Unit) {
        GlobalScope.launch {
            val setting = repository.getGlobalSetting()
            val list = listOf(CheckBoxItem("旧版主页主题", "应需求而来，喜欢0.9版本iFAFU界面就快来呀", setting.theme == GlobalSetting.THEME_OLD) {
                setting.theme = if (it) GlobalSetting.THEME_OLD else GlobalSetting.THEME_NEW
                isNeedCheckTheme = !isNeedCheckTheme
                GlobalScope.launch(Dispatchers.IO) {
                    repository.saveGlobalSetting(setting)
                }
            })
            success(list)
        }
    }

    fun ifNeedCheckTheme(check: () -> Unit) {
        if (isNeedCheckTheme) {
            check()
        }
    }
}