package cn.ifafu.ifafu.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.User

@Dao
interface ExamDao {
    @Query("SELECT * FROM Exam WHERE account=:account")
    fun getAll(account: String): List<Exam>

    @Query("SELECT * FROM Exam WHERE account=:account AND year=:year AND term=:term")
    fun getAll(account: String, year: String, term: String): List<Exam>


    @Query("SELECT * FROM Exam WHERE account=:account AND term=:term")
    fun getAllByTerm(account: String, term: String): List<Exam>

    @Query("SELECT * FROM Exam WHERE account=:account AND year=:year")
    fun getAllByYear(account: String, year: String): List<Exam>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg exam: Exam)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveScores(exam: List<Exam>)

    @Query("DELETE FROM Exam WHERE account=:account")
    fun delete(account: String)

    @Query("DELETE FROM Exam WHERE account=:account AND year=:year AND term=:term")
    fun delete(account: String, year: String, term: String)

    @Query("SELECT * FROM Exam WHERE year=:year AND term=:term AND account=:account")
    fun loadExams(account: String, year: String, term: String): LiveData<List<Exam>>

    @Query("SELECT * FROM User ORDER BY last_login_time DESC LIMIT 1")
    fun getUser(): User
}