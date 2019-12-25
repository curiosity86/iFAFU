package cn.ifafu.ifafu.mvp.setting

import android.content.Context
import cn.ifafu.ifafu.base.BaseModel
import cn.ifafu.ifafu.entity.GlobalSetting

class SettingModel(context: Context) : BaseModel(context), SettingContract.Model {

    override fun getSetting(): GlobalSetting {
        return repository.getGlobalSetting()
    }

    override fun save(setting: GlobalSetting) {
        repository.saveGlobalSetting(setting)
    }
}