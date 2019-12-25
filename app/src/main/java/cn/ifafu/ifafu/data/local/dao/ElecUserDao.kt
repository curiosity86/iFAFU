package cn.ifafu.ifafu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.entity.ElecUser

@Dao
interface ElecUserDao {
    @Query("SELECT * FROM ElecUser WHERE account=:account")
    fun elecUser(account: String): ElecUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(elecUser: ElecUser)
}