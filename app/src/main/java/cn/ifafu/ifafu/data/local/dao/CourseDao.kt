package cn.ifafu.ifafu.data.local.dao

import androidx.room.*
import cn.ifafu.ifafu.entity.Course

@Dao
interface CourseDao {
    @Query("SELECT * FROM Course WHERE account=:account")
    fun allCourses(account: String): List<Course>

    @Query("SELECT * FROM Course WHERE account=:account AND local=:local")
    fun allCourses(account: String, local: Boolean): List<Course>

    @Query("SELECT * FROM Course WHERE id=:id")
    fun course(id: Long): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg course: Course)

    @Delete
    fun delete(vararg course: Course)
}