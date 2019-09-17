package cn.ifafu.ifafu.mvp.syllabus_setting

import android.content.Context
import android.util.Log

import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.mvp.base.BaseZFModel
import com.alibaba.fastjson.JSONObject

class SyllabusSettingModel(context: Context) : BaseZFModel(context), SyllabusSettingContract.Model {

    override fun getSetting(): SyllabusSetting {
        var setting: SyllabusSetting? = repository.syllabusSetting
        if (setting == null) {
            setting = SyllabusSetting(repository.loginUser.account)
            repository.saveSyllabusSetting(setting)
        }
        Log.d(TAG, "getSetting: " + JSONObject.toJSONString(setting))
        return setting
    }

    override fun save(setting: SyllabusSetting) {
        repository.saveSyllabusSetting(setting)
    }
}
