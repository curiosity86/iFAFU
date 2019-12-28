package cn.ifafu.ifafu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.ifafu.ifafu.data.local.converter.IntListConverter
import cn.ifafu.ifafu.data.local.converter.IntTreeSetConverter
import cn.ifafu.ifafu.data.local.converter.LongListConverter
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
            ScoreFilter::class,
            SyllabusSetting::class,
            GlobalSetting::class,
            ElecQuery::class,
            ElecUser::class,
            ElecCookie::class
        ],
        version = 3,
        exportSchema = true
)
@TypeConverters(value = [
    IntTreeSetConverter::class,
    IntListConverter::class,
    StringMapConverter::class,
    LongListConverter::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val courseDao: CourseDao
    abstract val tokenDao: TokenDao
    abstract val examDao: ExamDao
    abstract val scoreDao: ScoreDao
    abstract val scoreFilterDao: ScoreFilterDao
    abstract val syllabusSettingDao: SyllabusSettingDao
    abstract val globalSettingDao: GlobalSettingDao
    abstract val elecQueryDao: ElecQueryDao
    abstract val elecUserDao: ElecUserDao
    abstract val elecCookieDao: ElecCookieDao

    companion object {

        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "ifafu_db")
                    .addMigrations(MIRGRATIONS_1_2, MIRGRATIONS_2_3)
                    .build()
        }

        private val MIRGRATIONS_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE Score")
                database.execSQL("CREATE TABLE IF NOT EXISTS Score (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `nature` TEXT NOT NULL, `attr` TEXT NOT NULL, `credit` REAL NOT NULL, `score` REAL NOT NULL, `makeupScore` REAL NOT NULL, `restudy` INTEGER NOT NULL, `institute` TEXT NOT NULL, `gpa` REAL NOT NULL, `remarks` TEXT NOT NULL, `makeupRemarks` TEXT NOT NULL, `isIESItem` INTEGER NOT NULL, `account` TEXT NOT NULL, `year` TEXT NOT NULL, `term` TEXT NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        private val MIRGRATIONS_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS ScoreFilter (`account` TEXT NOT NULL, `filterList` TEXT NOT NULL, PRIMARY KEY(`account`))")
            }
        }
    }

}