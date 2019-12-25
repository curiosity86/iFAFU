package cn.ifafu.ifafu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cn.ifafu.ifafu.data.local.converter.IntListConverter
import cn.ifafu.ifafu.data.local.converter.IntTreeSetConverter
import cn.ifafu.ifafu.data.local.converter.StringMapConverter
import cn.ifafu.ifafu.data.local.dao.*
import cn.ifafu.ifafu.entity.*

@Database(
        entities = [
            Course::class,
            User::class,
            Token::class,
            Exam::class,
            Score::class,
            SyllabusSetting::class,
            GlobalSetting::class,
            ElecQuery::class,
            ElecUser::class,
            ElecCookie::class
        ],
        version = 2,
        exportSchema = false
)
@TypeConverters(value = [
    IntTreeSetConverter::class,
    IntListConverter::class,
    StringMapConverter::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val courseDao: CourseDao
    abstract val tokenDao: TokenDao
    abstract val examDao: ExamDao
    abstract val scoreDao: ScoreDao
    abstract val syllabusSettingDao: SyllabusSettingDao
    abstract val globalSettingDao: GlobalSettingDao
    abstract val elecQueryDao: ElecQueryDao
    abstract val elecUserDao: ElecUserDao
    abstract val elecCookieDao: ElecCookieDao

//    class Builder {
//        val MIGRATION_1_TO_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.
//            }
//        }
//    }
}