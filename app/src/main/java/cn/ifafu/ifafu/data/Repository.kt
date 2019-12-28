package cn.ifafu.ifafu.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.HttpDataSource
import cn.ifafu.ifafu.data.http.elec.LoginService
import cn.ifafu.ifafu.data.http.elec.RetrofitFactory
import cn.ifafu.ifafu.data.http.parser.ElectivesParser
import cn.ifafu.ifafu.data.http.parser.ParamsParser
import cn.ifafu.ifafu.data.http.parser.ScoreParser
import cn.ifafu.ifafu.data.local.AppDatabase
import cn.ifafu.ifafu.data.local.LocalDataSource
import cn.ifafu.ifafu.entity.*
import cn.ifafu.ifafu.util.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object Repository : LocalDataSource, HttpDataSource {

    private lateinit var db: AppDatabase

    fun init(context: Context) {
        db = AppDatabase.getInstance(context)
    }

    var account: String = ""
        get() {
            return field.ifEmpty {
                field = SPUtils.get(Constant.SP_USER_INFO).getString("account")
                field
            }
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
            setting = SyllabusSetting(account)
            val isJS = getAllCourses().find { it.address.contains("旗教") } == null
            val times = ArrayList<Int>()
            Collections.addAll(times, *SyllabusSetting.intBeginTime[if (isJS) 1 else 0])
            setting.beginTime = times
        }
        return setting
    }

    override fun saveSyllabusSetting(syllabusSetting: SyllabusSetting) {
        db.syllabusSettingDao.save(syllabusSetting)
    }

    override suspend fun fetchParams(url: String, referer: String): MutableMap<String, String> = withContext(Dispatchers.IO) {
        val responseBody = APIManager.getZhengFangAPI().initParams2(url, referer).execute().body()!!
        val paramsParser = ParamsParser()
        paramsParser.parse(responseBody.string())
    }

    override suspend fun fetchElectives(): Electives = withContext(Dispatchers.IO) {
        val user = getInUseUser() ?: throw Exception("用户信息不存在")
        val queryUrl = School.getUrl(ZFApiList.ELECTIVES, user)
        val mainUrl = School.getUrl(ZFApiList.MAIN, user)
        val params = fetchParams(mainUrl, mainUrl)
        val html = APIManager.getZhengFangAPI()
                .getInfo2(queryUrl, mainUrl).execute()
                .body()!!.string()
        ElectivesParser(user).parse(html)
    }

    override suspend fun fetchScoreList(): List<Score> = withContext(Dispatchers.IO) {
        val user = getInUseUser() ?: throw Exception("用户信息不存在")
        val scoreUrl = School.getUrl(ZFApiList.SCORE, user)
        val mainUrl = School.getUrl(ZFApiList.MAIN, user)
        val params = fetchParams(scoreUrl, mainUrl)
        when (user.schoolCode) {
            School.FAFU -> {
                params["ddlxn"] = "全部"
                params["ddlxq"] = "全部"
                params["btnCx"] = ""
            }
            School.FAFU_JS -> {
                params["ddlXN"] = ""
                params["ddlXQ"] = ""
                params["ddl_kcxz"] = ""
                params["btn_zcj"] = ""
            }
        }
        val html = APIManager.getZhengFangAPI()
                .getInfo2(scoreUrl, scoreUrl, params).execute()
                .body()!!.string()
        val parser = ScoreParser(user)
        parser.parse(html).sortedBy { it.id }
    }

    override suspend fun fetchScoreList(year: String, term: String): List<Score> = withContext(Dispatchers.IO) {
        val scores = fetchScoreList()
        scores.filter { it.year == year && it.term == term }
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

    override suspend fun getScoreById(id: Long): Score = withContext(Dispatchers.IO) {
        db.scoreDao.getScoreById(id)
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

    override suspend fun getScoreFilter(): ScoreFilter {
        val account = account
        return db.scoreFilterDao.scoreFilter(account) ?: ScoreFilter().apply {
            this.account = account
        }
    }

    override suspend fun saveScoreFilter(scoreFilter: ScoreFilter) {
        db.scoreFilterDao.save(scoreFilter)
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

    override suspend fun deleteAccount(account: String) = withContext(Dispatchers.IO) {
        db.userDao.delete(account)
        db.courseDao.delete(account)
        db.examDao.delete(account)
        db.scoreDao.delete(account)
        db.globalSettingDao.delete(account)
        db.syllabusSettingDao.delete(account)
        db.tokenDao.delete(account)
        db.elecQueryDao.delete(account)
        db.elecUserDao.delete(account)
        db.elecCookieDao.delete(account)
    }

    override fun getNowYearTerm(): YearTerm {
        val yearList: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 8)
        val year = c[Calendar.YEAR]
        val yearByAccount: Int //通过学号判断学生
        val account = getInUseUser()?.account!!
        yearByAccount = if (account.length == 10) {
            account.substring(1, 3).toInt() + 2000
        } else {
            account.substring(0, 2).toInt() + 2000
        }
        for (i in 0 until year - yearByAccount) {
            yearList.add(String.format(Locale.CHINA, "%d-%d", year - i - 1, year - i))
        }
        val termList: MutableList<String> = ArrayList()
        for (i in 1..3) {
            termList.add(i.toString())
        }
        val termIndex = if (c[Calendar.MONTH] < 8) 0 else 1

        return YearTerm(yearList, termList, 0, termIndex)
    }

    override fun getYearTerm(): Pair<String, String> {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 6)
        val toTerm = if (c[Calendar.MONTH] < 8) "1" else "2"
        val year = c[Calendar.YEAR]
        val toYear = String.format("%d-%d", year - 1, year)
        return Pair(toYear, toTerm)
    }

    override suspend fun getGlobalSetting(): GlobalSetting = withContext(Dispatchers.IO) {
        db.globalSettingDao.globalSetting(account).run {
            if (this == null) {
                val setting = GlobalSetting(account)
                saveGlobalSetting(setting)
                setting
            } else {
                this
            }
        }
    }

    override suspend fun saveGlobalSetting(setting: GlobalSetting) = withContext(Dispatchers.IO) {
        db.globalSettingDao.save(setting)
    }

    /**
     * 电费查询
     */
    override fun getElecQuery(): ElecQuery? {
        return db.elecQueryDao.elecQuery(account)
    }

    override fun saveElecQuery(elecQuery: ElecQuery) {
        db.elecQueryDao.save(elecQuery)
    }

    override fun getElecCookie(): ElecCookie {
        var cookie = db.elecCookieDao.elecCookie(account)
        if (cookie == null) {
            cookie = ElecCookie()
            cookie.account = account
            saveElecCookie(cookie)
        }
        return cookie
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

    override suspend fun elecLoginInit() {
        withContext(Dispatchers.IO) {
            val service = RetrofitFactory.obtainService(LoginService::class.java, null)
            service.init("0", SPUtils.get(Constant.SP_ELEC).getString("IMEI"), "0").execute()
        }
    }

    override suspend fun elecLogin(account: String,
                                   password: String,
                                   verify: String): String = withContext(Dispatchers.IO) {
        val service = RetrofitFactory.obtainService(LoginService::class.java, null)
        service.login(
                "http://cardapp.fafu.edu.cn:8088/Phone/Login?sourcetype=0&IMEI=" +
                        SPUtils.get(Constant.SP_ELEC).getString("IMEI") + "&language=0",
                account, String(Base64.encode(password.toByteArray(), Base64.DEFAULT)),
                verify, "1", "1", "", "true"
        ).execute().body()!!.string()
    }

    override suspend fun elecVerifyBitmap(): Bitmap = withContext(Dispatchers.IO) {
        val service = RetrofitFactory.obtainService(LoginService::class.java, null)
        service.init("0", SPUtils.get(Constant.SP_ELEC).getString("IMEI"), "0").execute()
        val responseBody = service.verify(System.currentTimeMillis().toString()).execute().body()
        val bytes = responseBody!!.bytes()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}