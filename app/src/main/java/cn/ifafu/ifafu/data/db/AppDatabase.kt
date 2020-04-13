package cn.ifafu.ifafu.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.ifafu.ifafu.constant.DATABASE_NAME
import cn.ifafu.ifafu.data.db.converter.*
import cn.ifafu.ifafu.data.db.dao.*
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.experiment.data.db.ScoreDao

@Database(
        entities = [
            Course::class,
            User::class,
            Exam::class,
            Score::class,
            SyllabusSetting::class,
            GlobalSetting::class,
            ElecQuery::class,
            ElecUser::class,
            ElecCookie::class,
            Electives::class
        ],
        version = 7,
        exportSchema = true
)
@TypeConverters(value = [
    IntTreeSetConverter::class,
    IntListConverter::class,
    StringMapConverter::class,
    LongListConverter::class,
    LongHashSetConverter::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val courseDao: CourseDao
    abstract val examDao: ExamDao
    abstract val examDaoExp: cn.ifafu.ifafu.experiment.data.db.ExamDao
    abstract val scoreDao: ScoreDao
    abstract val syllabusSettingDao: SyllabusSettingDao
    abstract val globalSettingDao: GlobalSettingDao
    abstract val elecQueryDao: ElecQueryDao
    abstract val elecUserDao: ElecUserDao
    abstract val elecCookieDao: ElecCookieDao
    abstract val electivesDao: ElectivesDao
    abstract val userDaoExp: cn.ifafu.ifafu.experiment.data.db.UserDao

    companion object {

        @Volatile
        private lateinit var INSTANCE: AppDatabase

        fun getInstance(context: Context): AppDatabase {
            INSTANCE = if (::INSTANCE.isInitialized) INSTANCE else {
                Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                        .addMigrations(
                                MIRGRATIONS_1_2,
                                MIRGRATIONS_2_3,
                                MIRGRATIONS_3_4,
                                MIRGRATIONS_4_5,
                                MIRGRATIONS_5_6,
                                MIRGRATIONS_6_7)
                        .build()
            }
            return INSTANCE
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

        private val MIRGRATIONS_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS Electives (`account` TEXT NOT NULL, `total` INTEGER NOT NULL, `zrkx` INTEGER NOT NULL, `rwsk` INTEGER NOT NULL, `ysty` INTEGER NOT NULL, `wxsy` INTEGER NOT NULL, `cxcy` INTEGER NOT NULL, PRIMARY KEY(`account`))")
            }
        }

        private val MIRGRATIONS_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE ElecUser")
                database.execSQL("CREATE TABLE IF NOT EXISTS ElecUser (`account` TEXT NOT NULL, `xfbAccount` TEXT NOT NULL, `xfbId` TEXT NOT NULL, `password` TEXT NOT NULL, PRIMARY KEY(`account`))")
            }
        }
        private val MIRGRATIONS_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE User ADD last_login_time INTEGER NOT NULL DEFAULT 0;")
            }
        }
        private val MIRGRATIONS_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE ScoreFilter")
            }
        }
    }

}