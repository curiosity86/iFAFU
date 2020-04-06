package cn.ifafu.ifafu.experiment.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.ScoreFilter

@Dao
interface ScoreDao {

    @Query("SELECT * FROM ScoreFilter WHERE account=:account")
    fun getFilter(account: String): LiveData<ScoreFilter>

    @Query("SELECT * FROM Score WHERE account=:account AND id=:id")
    fun getScore(account: String, id: Long): LiveData<Score>

    @Query("SELECT * FROM Score WHERE account=:account")
    fun getAllScores(account: String): LiveData<List<Score>>

    @Query("SELECT * FROM Score WHERE account=:account AND year=:year AND term=:term")
    fun getAllScores(account: String, year: String, term: String): LiveData<List<Score>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveFilter(filter: ScoreFilter)
}