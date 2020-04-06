package cn.ifafu.ifafu.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.ScoreFilter

@Dao
interface ScoreDao {

    @Query("SELECT * FROM Score WHERE account=:account ORDER BY id")
    fun getAll(account: String): List<Score>

    @Query("SELECT * FROM Score WHERE account=:account AND term=:term AND year=:year ORDER BY id")
    fun getAll(account: String, year: String, term: String): List<Score>

    @Query("SELECT * FROM Score WHERE account=:account AND year=:year ORDER BY id")
    fun getAllByYear(account: String, year: String): List<Score>

    @Query("SELECT * FROM Score WHERE account=:account AND term=:term ORDER BY id")
    fun getAllByTerm(account: String, term: String): List<Score>

    @Query("SELECT * FROM Score WHERE id=:id")
    fun getScoreById(id: Int): Score


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg score: Score)

    @Delete
    fun delete(vararg score: Score)

    @Query("DELETE FROM Score WHERE account=:account")
    fun delete(account: String)

    @Query("DELETE FROM Score WHERE term=:term AND year=:year")
    fun delete(year: String, term: String)


    @Query("SELECT * FROM Score WHERE account=:account AND term=:term AND year=:year ORDER BY id")
    fun getAllLD(account: String, year: String, term: String): LiveData<List<Score>>

    @Query("SELECT * FROM ScoreFilter WHERE account=:account")
    fun getFilterByAccount(account: String): LiveData<ScoreFilter>
}