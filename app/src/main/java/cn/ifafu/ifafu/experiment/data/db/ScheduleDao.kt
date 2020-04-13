package cn.ifafu.ifafu.experiment.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import cn.ifafu.ifafu.data.entity.Score

@Dao
interface CourseDao {

    @Query("SELECT * FROM Course WHERE account=:account")
    fun loadAllCourses(account: String): LiveData<List<Score>>

}