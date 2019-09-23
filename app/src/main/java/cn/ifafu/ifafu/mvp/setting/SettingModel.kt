package cn.ifafu.ifafu.mvp.setting

import android.content.Context
import cn.ifafu.ifafu.base.BaseModel
import cn.ifafu.ifafu.data.entity.Setting

class SettingModel(context: Context) : BaseModel(context), SettingContract.Model {

    override fun getSetting(): Setting {
        return repository.setting
    }

    override fun save(setting: Setting) {
        repository.saveSetting(setting)
    }
}