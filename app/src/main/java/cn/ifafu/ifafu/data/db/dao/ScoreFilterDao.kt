package cn.ifafu.ifafu.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.ScoreFilter

@Dao
interface ScoreFilterDao {
    @Query("SELECT * FROM ScoreFilter WHERE account=:account")
    fun scoreFilter(account: String): ScoreFilter?

    @Query("SELECT * FROM ScoreFilter WHERE account=:account")
    fun get(account: String): LiveData<ScoreFilter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(scoreFilter: ScoreFilter)

    @Query("DELETE FROM ScoreFilter WHERE account=:account")
    fun delete(account: String)
}