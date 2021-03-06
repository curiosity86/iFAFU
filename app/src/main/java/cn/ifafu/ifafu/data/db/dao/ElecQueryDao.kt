package cn.ifafu.ifafu.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.ElecQuery

@Dao
interface ElecQueryDao {
    @Query("SELECT * FROM ElecQuery WHERE account=:account")
    fun elecQuery(account: String): ElecQuery?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(elecQuery: ElecQuery)

    @Query("DELETE FROM ElecQuery WHERE account=:account")
    fun delete(account: String)
}