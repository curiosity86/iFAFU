package cn.ifafu.ifafu.experiment.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import cn.ifafu.ifafu.data.db.dao.GlobalSettingDao
import cn.ifafu.ifafu.data.entity.GlobalSetting
import cn.ifafu.ifafu.experiment.data.UserManager

class SettingRepository(
        private val userManager: UserManager,
        private val settingDao: GlobalSettingDao) {

    fun loadSetting(): LiveData<GlobalSetting> {
        return userManager.userSwitchMap {
            settingDao.loadSetting(it.account)
        }
    }

    @WorkerThread
    fun saveSetting(setting: GlobalSetting) {
        settingDao.save(setting)
    }

}