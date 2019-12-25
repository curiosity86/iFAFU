package cn.ifafu.ifafu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.entity.Token

@Dao
interface TokenDao {

    @Query("SELECT * FROM Token WHERE account=:account")
    fun token(account: String): Token?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(token: Token)
}