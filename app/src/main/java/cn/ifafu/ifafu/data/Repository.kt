package cn.ifafu.ifafu.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.HttpDataSource
import cn.ifafu.ifafu.data.http.parser.*
import cn.ifafu.ifafu.data.local.AppDatabase
import cn.ifafu.ifafu.data.local.LocalDataSource
import cn.ifafu.ifafu.entity.*
import cn.ifafu.ifafu.entity.exception.VerifyException
import cn.ifafu.ifafu.util.SPUtils
import cn.ifafu.ifafu.util.encode
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URLEncoder
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
        db.userDao.save(user)
    }

    override fun saveLoginUser(user: User) {
        account = user.account
        db.userDao.save(user)
        SPUtils[Constant.SP_USER_INFO].putString("account", user.account)
    }

    override suspend fun elecCardBalance(): Response<Double> = withContext(Dispatchers.IO) {
        try {
            val responseBody = APIManager.xfbAPI.queryBalance("true").execute().body()
            val msg = JSONObject.parseObject(responseBody!!.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_card")
                    .getJSONArray("card")
                    .getJSONObject(0)
            return@withContext Response.success((msg.getIntValue("db_balance") + msg.getIntValue("unsettle_amount")) / 100.0)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Response.failure<Double>("校园卡余额获取出错")
        }
    }

    override suspend fun checkLoginStatus(): Boolean = withContext(Dispatchers.IO) {
        val elecCookie = getElecCookie()
        elecCookieInit()
        val html = APIManager.xfbAPI.page(
                "31", "3", "2", "", "electricity",
                URLEncoder.encode("交电费", "gbk"),
                elecCookie["sourcetypeticket"],
                SPUtils.get(Constant.SP_ELEC).getString("IMEI"),
                "0", "1"
        ).execute().body()!!.string()
        !(html.contains("<title>登录</title>") || html.contains("建设中~~~"))
    }

    override suspend fun fetchElectricityInfo(query: ElecQuery): Response<String> = withContext(Dispatchers.IO) {
        val responseBody = APIManager.xfbAPI.query(
                query.toFiledMap(ElecQuery.Query.ROOMINFO)
        ).execute().body()
        try {
            Response.success(JSONObject.parseObject(responseBody!!.string())
                    .getJSONObject("Msg")
                    .getJSONObject("query_elec_roominfo")
                    .getString("errmsg"))
        } catch (e: Exception) {
            Response.error("查询出错")
        }
    }

    override suspend fun getElectives(): Electives? {
        return db.electivesDao.electives(account)
    }

    override suspend fun saveElectives(electives: Electives) {
        db.electivesDao.save(electives)
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

    override suspend fun login(account: String, password: String): Response<String> = withContext(Dispatchers.IO) {
        val user = User().apply {
            this.account = if (account.getOrNull(0) == '0') account.drop(1) else account
            this.password = password
            if (account.length == 9) {
                this.schoolCode = School.FAFU_JS
            } else if (account.length == 10) {
                this.schoolCode = School.FAFU
            }
        }
        val loginUrl = School.getUrl(ZFApiList.LOGIN, user)
        val verifyUrl = School.getUrl(ZFApiList.VERIFY, user)
        val params = fetchParams(loginUrl).apply {
            put("txtUserName", user.account)
            put("Textbox1", "")
            put("TextBox2", user.password)
            put("RadioButtonList1", "")
            put("Button1", "")
            put("lbLanguage", "")
            put("hidPdrs", "")
            put("hidsc", "")
            for (entry in entries) {
                if (entry.value.isNotEmpty()) {
                    entry.setValue(entry.value.encode("GBK"))
                }
            }
        }
        val verifyParser = VerifyParser(BaseApplication.appContext).apply { init() }
        val loginParser = LoginParser()
        var repeat = 0
        //验证码错误，则重复登录，次数限制10次
        while (repeat++ < 10) {
            try {
                val verifyCode = verifyParser.parse(
                        APIManager.zhengFangAPI
                                .getCaptcha2(verifyUrl)
                                .execute())
                params["txtSecretCode"] = verifyCode
                return@withContext loginParser.parse(
                        APIManager.zhengFangAPI
                                .login2(loginUrl, params)
                                .execute().body()!!
                                .string()
                )
            } catch (e: VerifyException) {
                continue
            } catch (e: IOException) {
                return@withContext Response.error("教务管理系统又崩溃了")
            }
        }
        return@withContext Response.error("登录出错")
    }

    override suspend fun fetchParams(url: String): MutableMap<String, String> = withContext(Dispatchers.IO) {
        val responseBody = APIManager.zhengFangAPI.initParams2(url).execute().body()!!
        val paramsParser = ParamsParser()
        paramsParser.parse(responseBody.string())
    }

    override suspend fun fetchParams(url: String, referer: String): MutableMap<String, String> = withContext(Dispatchers.IO) {
        val response = APIManager.zhengFangAPI.initParams2(url, referer).execute()
        val paramsParser = ParamsParser2()
        paramsParser.parse(response)
    }

    override suspend fun fetchElectives(): Electives = withContext(Dispatchers.IO) {
        val user = getInUseUser() ?: throw Exception("用户信息不存在")
        val queryUrl = School.getUrl(ZFApiList.ELECTIVES, user)
        val mainUrl = School.getUrl(ZFApiList.MAIN, user)
        val html = APIManager.zhengFangAPI
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
        val html = APIManager.zhengFangAPI
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

    override suspend fun getScoreById(id: Long): Score? = withContext(Dispatchers.IO) {
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
        db.electivesDao.delete(account)
    }

    override fun getNowYearTerm(): YearTerm {
        val yearList: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        val termIndex = if (c[Calendar.MONTH] < 1 || c[Calendar.MONTH] > 6) 0 else 1
        c.add(Calendar.MONTH, 5)
        val year = c[Calendar.YEAR]
        val yearByAccount: Int //通过学号判断学生
        val account = account
        yearByAccount = if (account.length == 10) {
            account.substring(1, 3).toInt() + 2000
        } else {
            account.substring(0, 2).toInt() + 2000
        }
        for (i in yearByAccount until year) {
            yearList.add(0, String.format(Locale.CHINA, "%d-%d", i, i + 1))
        }
        val termList = listOf("1", "2", "全部")
        yearList.add("全部")
        return YearTerm(yearList, termList, 0, termIndex)
    }

    override fun getSelectionList(): List<ElecSelection> {
        return listOf(
                ElecSelection(aid = "0030000000002501", name = "常工电子电控", areaId = "农林大学", area = "农林大学", buildingId = "1", building = "北区1号楼", group1 = "北区", group2 = "北区1号楼"),
                ElecSelection(aid = "0030000000002501", name = "常工电子电控", areaId = "农林大学", area = "农林大学", buildingId = "2", building = "北区2号楼", group1 = "北区", group2 = "北区2号楼"),
                ElecSelection(aid = "0030000000002501", name = "常工电子电控", areaId = "农林大学", area = "农林大学", buildingId = "953", building = "南区10号楼", group1 = "南区", group2 = "南区10号楼"),
                ElecSelection(aid = "0030000000002501", name = "常工电子电控", areaId = "农林大学", area = "农林大学", buildingId = "954", building = "南区3号楼", group1 = "南区", group2 = "南区3号楼"),
                ElecSelection(aid = "0030000000002501", name = "常工电子电控", areaId = "农林大学", area = "农林大学", buildingId = "955", building = "南区4号楼", group1 = "南区", group2 = "南区4号楼"),
                ElecSelection(aid = "0030000000002501", name = "常工电子电控", areaId = "农林大学", area = "农林大学", buildingId = "956", building = "桃山3号楼", group1 = "桃山", group2 = "桃山13号楼"),

                ElecSelection(aid = "0030000000008001", name = "山东科大电子电控", buildingId = "桃山区", building = "桃山区", floor = "7#", floorId = "7#", group1 = "桃山", group2 = "桃山7号楼"),
                ElecSelection(aid = "0030000000008001", name = "山东科大电子电控", buildingId = "桃山区", building = "桃山区", floor = "8#", floorId = "8#", group1 = "桃山", group2 = "桃山8号楼"),

                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "7", building = "下安5号", group1 = "下安", group2 = "下安5号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "3", building = "下安1号", group1 = "下安", group2 = "下安1号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "10", building = "北区4号", group1 = "北区", group2 = "北区4号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "1", building = "南区2号", group1 = "南区", group2 = "南区2号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "9", building = "北区3号", group1 = "北区", group2 = "北区3号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "11", building = "北区5号", group1 = "北区", group2 = "北区5号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "6", building = "下安4号", group1 = "下安", group2 = "下安4号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "2", building = "南区1号", group1 = "南区", group2 = "南区1号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "8", building = "下安6号", group1 = "下安", group2 = "下安6号楼"),
                ElecSelection(aid = "0030000000008101", name = "开普电子电控", areaId = "0", area = "本校区", buildingId = "5", building = "下安2号", group1 = "下安", group2 = "下安2号楼"),

                ElecSelection(aid = "0030000000008102", name = "开普电控东苑", buildingId = "1", building = "东苑1#楼", group1 = "东苑", group2 = "东苑1号楼"),
                ElecSelection(aid = "0030000000008102", name = "开普电控东苑", buildingId = "5", building = "东苑2#楼", group1 = "东苑", group2 = "东苑2号楼"),
                ElecSelection(aid = "0030000000008102", name = "开普电控东苑", buildingId = "6", building = "东苑3#楼", group1 = "东苑", group2 = "东苑3号楼"),
                ElecSelection(aid = "0030000000008102", name = "开普电控东苑", buildingId = "7", building = "东苑4#楼", group1 = "东苑", group2 = "东苑4号楼"),
                ElecSelection(aid = "0030000000008102", name = "开普电控东苑", buildingId = "8", building = "东苑5#楼", group1 = "东苑", group2 = "东苑5号楼"),
                ElecSelection(aid = "0030000000008102", name = "开普电控东苑", buildingId = "9", building = "东苑6#楼", group1 = "东苑", group2 = "东苑6号楼"),
                ElecSelection(aid = "0030000000008102", name = "开普电控东苑", buildingId = "10", building = "东苑7#楼", group1 = "东苑", group2 = "东苑7号楼"),
                ElecSelection(aid = "0030000000008102", name = "开普电控东苑", buildingId = "11", building = "东苑8#楼", group1 = "东苑", group2 = "东苑8号楼")
        )
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

    override suspend fun elecCookieInit() {
        withContext(Dispatchers.IO) {
            val service = APIManager.xfbAPI
            service.init("0", SPUtils.get(Constant.SP_ELEC).getString("IMEI"), "0").execute()
        }
    }

    override suspend fun elecLogin(account: String,
                                   password: String,
                                   verify: String): String = withContext(Dispatchers.IO) {
        val service = APIManager.xfbAPI
        service.login(
                "http://cardapp.fafu.edu.cn:8088/Phone/Login?sourcetype=0&IMEI=" +
                        SPUtils.get(Constant.SP_ELEC).getString("IMEI") + "&language=0",
                account, String(Base64.encode(password.toByteArray(), Base64.DEFAULT)),
                verify, "1", "1", "", "true"
        ).execute().body()!!.string()
    }

    override suspend fun elecVerifyBitmap(): Bitmap = withContext(Dispatchers.IO) {
        val service = APIManager.xfbAPI
        service.init("0", SPUtils.get(Constant.SP_ELEC).getString("IMEI"), "0").execute()
        val responseBody = service.verify(System.currentTimeMillis().toString()).execute().body()
        val bytes = responseBody!!.bytes()
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

}