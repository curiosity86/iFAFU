package cn.ifafu.ifafu.data.local.dao

import androidx.room.*
import cn.ifafu.ifafu.data.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User ORDER BY account")
    fun allUser(): List<User>

    @Query("SELECT * FROM User WHERE account=:account")
    fun user(account: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg user: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM User WHERE account=:account")
    fun delete(account: String)
}