package cn.ifafu.ifafu.experiment.data.db

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
    fun getAllExams(account: String): LiveData<List<Exam>>

    @Query("SELECT * FROM Exam WHERE account=:account AND year=:year AND term=:term")
    fun getAllExams(account: String, year: String, term: String): LiveData<List<Exam>>


    @Query("SELECT * FROM Exam WHERE account=:account AND term=:term")
    fun getAllExamsByTerm(account: String, term: String): LiveData<List<Exam>>

    @Query("SELECT * FROM Exam WHERE account=:account AND year=:year")
    fun getAllExamsByYear(account: String, year: String): LiveData<List<Exam>>

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