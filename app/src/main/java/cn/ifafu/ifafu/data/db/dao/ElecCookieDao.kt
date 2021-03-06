package cn.ifafu.ifafu.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.ElecCookie

@Dao
interface ElecCookieDao {
    @Query("SELECT * FROM ElecCookie WHERE account=:account")
    fun elecCookie(account: String): ElecCookie?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(elecCookie: ElecCookie)

    @Query("DELETE FROM ElecCookie WHERE account=:account")
    fun delete(account: String)
}