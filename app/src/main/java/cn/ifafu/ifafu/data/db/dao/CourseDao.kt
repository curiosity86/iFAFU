package cn.ifafu.ifafu.data.db.dao

import androidx.room.*
import cn.ifafu.ifafu.data.entity.Course

@Dao
interface CourseDao {
    @Query("SELECT * FROM Course WHERE account=:account")
    fun getAll(account: String): List<Course>

    @Query("SELECT * FROM Course WHERE account=:account AND local=:local")
    fun getAll(account: String, local: Boolean): List<Course>

    @Query("SELECT * FROM Course WHERE id=:id")
    fun course(id: Long): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg course: Course)

    @Delete
    fun delete(vararg course: Course)

    @Query("DELETE FROM Course WHERE account=:account")
    fun delete(account: String)

    @Query("DELETE FROM Course WHERE account=:account AND local=:local")
    fun delete(account: String, local: Boolean)
}