package cn.ifafu.ifafu.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.SyllabusSetting

@Dao
interface SyllabusSettingDao {
    @Query("SELECT * FROM SyllabusSetting WHERE account=:account")
    fun syllabusSetting(account: String): SyllabusSetting?

    @Query("SELECT * FROM SyllabusSetting WHERE account=:account")
    fun getSetting(account: String): LiveData<SyllabusSetting>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(syllabusSetting: SyllabusSetting)

    @Query("DELETE FROM SyllabusSetting WHERE account=:account")
    fun delete(account: String)
}