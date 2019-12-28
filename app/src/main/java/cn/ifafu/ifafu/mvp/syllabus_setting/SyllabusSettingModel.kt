package cn.ifafu.ifafu.mvp.syllabus_setting

import android.content.Context
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.entity.SyllabusSetting

class SyllabusSettingModel(context: Context) : BaseZFModel(context), SyllabusSettingContract.Model {

    override fun getSetting(): SyllabusSetting {
        return repository.getSyllabusSetting()
    }

    override suspend fun save(setting: SyllabusSetting) {
        repository.saveSyllabusSetting(setting)
    }
}
