package cn.ifafu.ifafu.experiment.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.Score

@Dao
interface ScoreDao {

    @Query("SELECT * FROM Score WHERE id=:id ORDER BY id")
    fun getScore(id: Int): LiveData<Score>

    @Query("SELECT * FROM Score WHERE account=:account ORDER BY id")
    fun getAllScores(account: String): LiveData<List<Score>>

    @Query("SELECT * FROM Score WHERE account=:account AND year=:year ORDER BY id")
    fun getAllScoresByYear(account: String, year: String): LiveData<List<Score>>

    @Query("SELECT * FROM Score WHERE account=:account AND term=:term ORDER BY id")
    fun getAllScoresByTerm(account: String, term: String): LiveData<List<Score>>

    @Query("SELECT * FROM Score WHERE account=:account AND year=:year AND term=:term ORDER BY id")
    fun getAllScores(account: String, year: String, term: String): LiveData<List<Score>>

    @Query("SELECT * FROM Score WHERE id=:id ORDER BY id")
    fun getScoreL(id: Int): Score

    @Query("SELECT * FROM Score WHERE account=:account ORDER BY id")
    fun getAllScoresL(account: String): List<Score>

    @Query("SELECT * FROM Score WHERE account=:account AND year=:year ORDER BY id")
    fun getAllScoresByYearL(account: String, year: String): List<Score>

    @Query("SELECT * FROM Score WHERE account=:account AND term=:term ORDER BY id")
    fun getAllScoresByTermL(account: String, term: String): List<Score>

    @Query("SELECT * FROM Score WHERE account=:account AND year=:year AND term=:term ORDER BY id")
    fun getAllScoresL(account: String, year: String, term: String): List<Score>

    @Query("DELETE FROM Score WHERE account=:account AND year=:year AND term=:term")
    fun deleteScore(account: String, year: String, term: String)

    @Query("DELETE FROM Score WHERE account=:account")
    fun deleteScore(account: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveScore(vararg score: Score)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveScore(scores: List<Score>)

}