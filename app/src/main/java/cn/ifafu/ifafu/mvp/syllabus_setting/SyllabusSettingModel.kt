package cn.ifafu.ifafu.mvp.syllabus_setting

import android.content.Context
import cn.ifafu.ifafu.base.mvp.BaseModel
import cn.ifafu.ifafu.entity.SyllabusSetting

class SyllabusSettingModel(context: Context) : BaseModel(context), SyllabusSettingContract.Model {

    override fun getSetting(): SyllabusSetting {
        return mRepository.getSyllabusSetting()
    }

    override suspend fun save(setting: SyllabusSetting) {
        mRepository.saveSyllabusSetting(setting)
    }
}
