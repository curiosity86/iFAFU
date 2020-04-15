package cn.ifafu.ifafu.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.GlobalSetting

@Dao
interface GlobalSettingDao {

    @Query("SELECT * FROM GlobalSetting WHERE account=:account")
    fun globalSetting(account: String): GlobalSetting?


    @Query("SELECT * FROM GlobalSetting WHERE account=:account")
    fun loadSetting(account: String): LiveData<GlobalSetting>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(setting: GlobalSetting)

    @Query("DELETE FROM GlobalSetting WHERE account=:account")
    fun delete(account: String)
}