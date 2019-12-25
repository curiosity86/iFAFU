package cn.ifafu.ifafu.data

import android.content.Context
import androidx.room.Room
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.data.local.AppDatabase
import cn.ifafu.ifafu.entity.*
import cn.ifafu.ifafu.util.SPUtils
import java.util.*

object RepositoryImpl : Repository {

    private lateinit var db: AppDatabase

    fun init(context: Context) {
        db = Room.databaseBuilder(context, AppDatabase::class.java, "ifafu")
                .fallbackToDestructiveMigration()
                .build()
    }


    var account: String = ""
        get() {
            return field.ifEmpty { SPUtils.get(Constant.SP_USER_INFO).getString("account") }
        }
        private set

    override fun getInUseUser(): User? {
        return db.userDao.user(account)
    }

    override fun getAllUsers(): List<User> {
        return db.userDao.allUser()
    }

    override fun saveUser(user: User) {
        account = user.account
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.account)
        db.userDao.save(user)
    }

    override fun saveLoginUser(user: User) {
        account = user.account
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.account)
    }

    override fun deleteUser(user: User) {
        db.userDao.delete(user)
    }

    override fun getAllCourses(): List<Course> {
        return db.courseDao.allCourses(account)
    }

    override fun getCourses(local: Boolean): List<Course> {
        return db.courseDao.allCourses(account, local)
    }

    override fun getCourseById(id: Long): Course? {
        return db.courseDao.course(id)
    }

    override fun saveCourse(course: Course) {
        db.courseDao.save(course)
    }

    override fun saveCourse(courses: List<Course>) {
        db.courseDao.save(*courses.toTypedArray())
    }

    override fun deleteCourse(courses: List<Course>) {
        db.courseDao.delete(*courses.toTypedArray())
    }

    override fun deleteCourse(course: Course) {
        db.courseDao.delete(course)
    }

    override fun deleteAllOnlineCourse() {
        deleteCourse(getCourses(false))
    }

    override fun getSyllabusSetting(): SyllabusSetting {
        var setting = db.syllabusSettingDao.syllabusSetting(account)
        if (setting == null) {
            setting = SyllabusSetting()
            setting.account = account
            var js = false
            for (c in getAllCourses()) {
                if (c.address.contains("旗教")) {
                    js = true
                    break
                }
            }
            val times = ArrayList<Int>()
            Collections.addAll(times, *SyllabusSetting.intBeginTime[if (js) 1 else 0])
            setting.beginTime = times
        }
        return setting
    }

    override fun saveSyllabusSetting(syllabusSetting: SyllabusSetting) {
        db.syllabusSettingDao.save(syllabusSetting)
    }

    override fun getAllScores(): List<Score> {
        return db.scoreDao.allScores(account)
    }

    override fun getScoresByYear(year: String): List<Score> {
        return db.scoreDao.allScoresByYear(account, year)
    }

    override fun getScoresByTerm(term: String): List<Score> {
        return db.scoreDao.allScoresByTerm(account, term)
    }

    override fun getScoreById(id: Long): Score {
        return db.scoreDao.getScoreById(id)
    }

    override fun getScores(year: String, term: String): List<Score> {
        return db.scoreDao.allScores(account, year, term)
    }

    override fun deleteScore(year: String, term: String) {
        db.scoreDao.delete(year, term)
    }

    override fun deleteAllScore() {
        db.scoreDao.delete(account)
    }

    override fun saveScore(score: Score) {
        db.scoreDao.save(score)
    }

    override fun saveScore(scores: List<Score>) {
        db.scoreDao.save(*scores.toTypedArray())
    }

    override fun getAllExams(): List<Exam> {
        return db.examDao.allExams(account)
    }

    override fun getExams(year: String, term: String): List<Exam> {
        return db.examDao.allExams(account, year, term)
    }

    override fun saveExam(exams: List<Exam>) {
        db.examDao.save(*exams.toTypedArray())
    }

    override fun getToken(account: String): Token? {
        return db.tokenDao.token(account)
    }

    override fun saveToken(token: Token) {
        db.tokenDao.save(token)
    }

    override fun clearAllData() {
//        for (allDao in DaoManager.getInstance().daoSession.getAllDaos()) {
//            allDao.deleteAll()
//        }
    }

    override fun getYearTermList(): YearTerm {
        val yearList: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 8)
        val year = c[Calendar.YEAR]
        val acYear: Int //几几届学生
        val account = getInUseUser()?.account!!
        acYear = if (account.length == 10) {
            account.substring(1, 3).toInt() + 2000
        } else {
            account.substring(0, 2).toInt() + 2000
        }
        for (i in 0 until year - acYear) {
            yearList.add(String.format(Locale.CHINA, "%d-%d", year - i - 1, year - i))
        }
        val termList: MutableList<String> = ArrayList()
        for (i in 1..3) {
            termList.add(i.toString())
        }
        return YearTerm(yearList, termList)
    }

    override fun getYearTerm(): Pair<String, String> {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 6)
        val toTerm = if (c[Calendar.MONTH] < 8) "1" else "2"
        val year = c[Calendar.YEAR]
        val toYear = String.format("%d-%d", year - 1, year)
        return Pair(toYear, toTerm)
    }

    override fun getElecQuery(): ElecQuery? {
        return db.elecQueryDao.elecQuery(account)
    }

    override fun saveElecQuery(elecQuery: ElecQuery) {
        db.elecQueryDao.save(elecQuery)
    }

    override fun getElecCookie(): ElecCookie? {
        return db.elecCookieDao.elecCookie(account)
    }

    override fun saveElecCookie(cookie: ElecCookie) {
        db.elecCookieDao.save(cookie)
    }

    override fun getElecUser(): ElecUser? {
        return db.elecUserDao.elecUser(account)
    }

    override fun saveElecUser(elecUser: ElecUser) {
        db.elecUserDao.save(elecUser)
    }

    override fun getGlobalSetting(): GlobalSetting {
        var setting = db.globalSettingDao.globalSetting(account)
        if (setting == null) {
            setting = GlobalSetting()
            setting.account = account
            saveGlobalSetting(setting)
        }
        return setting
    }

    override fun saveGlobalSetting(setting: GlobalSetting) {
        db.globalSettingDao.save(setting)
    }

}