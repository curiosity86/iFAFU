package cn.ifafu.ifafu.data.db.dao

import androidx.room.*
import cn.ifafu.ifafu.data.entity.Course

@Dao
abstract class CourseDao {


    @Query("SELECT * FROM Course WHERE account=:account")
    abstract fun getAll(account: String): List<Course>

    @Query("SELECT * FROM Course WHERE account=:account AND local=:local")
    abstract fun getAll(account: String, local: Boolean): List<Course>

    @Query("SELECT * FROM Course WHERE id=:id")
    abstract fun get(id: Int): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun save(vararg course: Course)

    @Delete
    abstract fun delete(vararg course: Course)

    @Query("DELETE FROM Course WHERE account=:account")
    abstract fun delete(account: String)

    @Query("DELETE FROM Course WHERE account=:account AND local=:local")
    abstract fun delete(account: String, local: Boolean)
}