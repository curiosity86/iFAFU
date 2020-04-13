package cn.ifafu.ifafu.experiment.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User ORDER BY last_login_time DESC LIMIT 1")
    fun getUser(): LiveData<User>

    @Query("SELECT * FROM User")
    fun getAllUsers(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: User)

    @Query("UPDATE User SET last_login_time=:lastLoginTime WHERE account=:account")
    fun updateUserLastLoginTime(account: String, lastLoginTime: Long)

}