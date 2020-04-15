package cn.ifafu.ifafu.experiment.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.data.db.dao.SyllabusSettingDao
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.experiment.data.UserManager
import kotlinx.coroutines.Dispatchers

class CourseRepository(
        private val userManager: UserManager,
        private val syllabusSettingDao: SyllabusSettingDao) {

    val setting: LiveData<SyllabusSetting>
        get() = _setting
    private val _setting = MediatorLiveData<SyllabusSetting>().apply {
        addSource(userManager.user) {
            addSource(liveData<SyllabusSetting>(Dispatchers.IO) {
                var setting = syllabusSettingDao.syllabusSetting(it.account)
                if (setting == null) {
                    setting = SyllabusSetting(it.account)
                    syllabusSettingDao.save(setting)
                }
                emit(setting)
            }) { setting ->
                this.value = setting
            }
        }
    }

    fun saveSetting(setting: SyllabusSetting) {
        syllabusSettingDao.save(setting)
        if (setting != _setting.value) {
            _setting.postValue(setting)
        }
    }
}