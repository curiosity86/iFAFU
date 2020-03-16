package cn.ifafu.ifafu.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.Token

@Dao
interface TokenDao {

    @Query("SELECT * FROM Token WHERE account=:account")
    fun token(account: String): Token?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(token: Token)

    @Query("DELETE FROM Token WHERE account=:account")
    fun delete(account: String)
}