package cn.ifafu.ifafu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.ifafu.ifafu.entity.Exam

@Dao
interface ExamDao {
    @Query("SELECT * FROM Exam WHERE account=:account")
    fun allExams(account: String): List<Exam>

    @Query("SELECT * FROM Exam WHERE account=:account AND year=:year AND term=:term")
    fun allExams(account: String, year: String, term: String): List<Exam>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg exam: Exam)
}