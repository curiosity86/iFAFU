package cn.ifafu.ifafu.data.repository.impl

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.data.IFResult
import cn.ifafu.ifafu.data.bean.*
import cn.ifafu.ifafu.data.db.AppDatabase
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.data.exception.VerifyException
import cn.ifafu.ifafu.data.new_http.JWService
import cn.ifafu.ifafu.data.new_http.WoService
import cn.ifafu.ifafu.data.new_http.impl.JWServiceImpl
import cn.ifafu.ifafu.data.new_http.impl.WoServiceImpl
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.retrofit.APIManager
import cn.ifafu.ifafu.data.retrofit.parser.*
import cn.ifafu.ifafu.ui.main.bean.Weather
import cn.ifafu.ifafu.ui.schedule.view.ScheduleItem
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.util.HttpClient
import cn.ifafu.ifafu.util.SPUtils
import cn.ifafu.ifafu.util.encode
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
object RepositoryImpl : Repository {

    private var context: Context = BaseApplication.appContext

    private val db: AppDatabase by lazy { AppDatabase.getInstance(context) }
    val user by lazy { UserRt(context) }
    val syllabus by lazy { SyllabusRt(context) }
    val exam by lazy { ExamRt() }

    private val woService: WoService = WoServiceImpl()
    private val jwService: JWService = JWServiceImpl()

    private var account: String = ""
        get() {
            return field.ifEmpty {
                field = SPUtils[Constant.SP_USER_INFO].getString("account")
                field
            }
        }

    private val userLD = liveData(Dispatchers.IO) {
        user.getInUse()?.let { emit(it) }
    } as MutableLiveData<User>

    /**
     * 启动App时初始化
     */
    fun init(context: Context) {
        RepositoryImpl.context = context
        GlobalScope.launch(Dispatchers.IO) {
            user.getInUse()?.let { jwService.checkoutTo(it) }
        }
    }

    private suspend fun fetchParams(url: String): MutableMap<String, String> = withContext(Dispatchers.IO) {
        val responseBody = APIManager.zhengFangAPI.get(url).execute().body()!!
        val paramsParser = ParamsParser()
        paramsParser.parse(responseBody.string())
    }

    private suspend fun fetchParams(url: String, referer: String): MutableMap<String, String> = withContext(Dispatchers.IO) {
        val response = APIManager.zhengFangAPI.get(url, referer).execute()
        val paramsParser = ParamsParser2()
        paramsParser.parse(response)
    }

    fun getNowSemester(): Semester {
        val yearList: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        val termIndex = if (c[Calendar.MONTH] < 1 || c[Calendar.MONTH] > 6) 0 else 1
        c.add(Calendar.MONTH, 5)
        val year = c[Calendar.YEAR]
        val yearByAccount: Int //通过学号判断学生入学年份
        val account = account
        yearByAccount = if (account.length == 10) {
            account.substring(1, 3).toInt() + 2000
        } else {
            account.substring(0, 2).toInt() + 2000
        }
        for (i in yearByAccount until year) {
            yearList.add(0, String.format(Locale.CHINA, "%d-%d", i, i + 1))
        }
        yearList.add("全部")
        return Semester(yearList, arrayListOf("1", "2", "全部"), 0, termIndex)
    }

    fun getExamSemester(): Semester {
        val yearList: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        val termIndex = if (c[Calendar.MONTH] < 1 || c[Calendar.MONTH] > 6) 0 else 1
        c.add(Calendar.MONTH, 5)
        val year = c[Calendar.YEAR]
        val yearByAccount: Int //通过学号判断学生入学年份
        val account = account
        yearByAccount = if (account.length == 10) {
            account.substring(1, 3).toInt() + 2000
        } else {
            account.substring(0, 2).toInt() + 2000
        }
        for (i in yearByAccount until year) {
            yearList.add(0, String.format(Locale.CHINA, "%d-%d", i, i + 1))
        }
        return Semester(yearList, arrayListOf("1", "2"), 0, termIndex)
    }

    class SyllabusRt(context: Context) {

        private val dao = AppDatabase.getInstance(context).courseDao
        private val settingDao = AppDatabase.getInstance(context).syllabusSettingDao

        suspend fun fetchAll(): Response<List<Course>> = withContext(Dispatchers.IO) {
            val user = user.getInUse() ?: throw Exception("用户信息不存在")
            val url: String = Constant.getUrl(ZFApiList.SYLLABUS, user)
            val headerReferer: String = Constant.getUrl(ZFApiList.MAIN, user)
            val resp = APIManager.zhengFangAPI.get(url, headerReferer).execute()
            if (resp.code() == 302) {
                return@withContext Response.failure<List<Course>>("无法获取课表信息")
            }
            val html = resp.body()!!.string()
            SyllabusParser(user).parse(html).apply {
                //若不为空，则自动保存
                if (!data.isNullOrEmpty()) {
                    deleteNetwork()
                    save(data)
                    //更新原有课表设置，注意与getSetting的区别
                    val syllabusSetting = settingDao.syllabusSetting(account)
                            ?: SyllabusSetting(account)
                    saveSetting(syllabusSetting.apply {
                        //若教室存在旗教字样，则为旗山校区
                        val isBenBu = getAll().find { it.address.contains("旗教") } == null
                        beginTime = SyllabusSetting.intBeginTime[if (isBenBu) 0 else 1]
                    })
                }
            }
        }

        suspend fun get(id: Int): Course? = withContext(Dispatchers.Default) {
            dao.get(id)
        }

        suspend fun save(course: Course) = withContext(Dispatchers.Default) {
            dao.save(course)
        }

        private suspend fun save(courses: List<Course>) = withContext(Dispatchers.Default) {
            dao.save(*courses.toTypedArray())
        }

        suspend fun getSetting(): SyllabusSetting = withContext(Dispatchers.Default) {
            val setting = settingDao.syllabusSetting(account) ?: SyllabusSetting(account)
            //若教室存在旗教字样，则为旗山校区
            val isBenBu = dao.getAll(account).find { it.address.contains("旗教") } == null
            setting.beginTime = SyllabusSetting.intBeginTime[if (isBenBu) 0 else 1]
            setting
        }

        fun getHoliday(): List<Vocation> {
            return listOf(
                    Vocation("清明节", "2020-04-04", 3),
                    Vocation("劳动节", "2020-05-01", 5,
                            mapOf("2020-05-05" to "2019-05-09")
                    ),
                    Vocation("端午节", "2020-06-25", 3,
                            mapOf("2020-06-26" to "2019-06-28")
                    )
            )
        }

        /**
         * 获取调课方式
         * Map<fromWeek+fromWeekday, toWeek+toWeekday>
         * @return MutableMap<fromWeek, MutableMap<fromWeekday, Pair<toWeek, toWeekday>>>
         *     把(fromWeek,fromWeekday)的课调到(toWeek,toWeekday)
         *     放假则Pair<toWeek, toWeekday>为null
         */
        suspend fun getAdjustmentInfo(): Map<Int, Map<Int, Pair<Int, Int>?>> = withContext(Dispatchers.Default) {
            //MutableMap<fromWeek, MutableMap<fromWeekday, Pair<toWeek, toWeekday>>>
            @SuppressLint("UseSparseArrays")
            val holidays = getHoliday()
            val fromToMap: MutableMap<Int, MutableMap<Int, Pair<Int, Int>?>> = HashMap()
            val setting: SyllabusSetting = getSetting()
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            val openingDate: Date = format.parse(setting.openingDay)
            val calendar: Calendar = Calendar.getInstance()
            calendar.firstDayOfWeek = setting.firstDayOfWeek
            for (holiday in holidays) {
                for ((key, value) in holiday.fromTo) {
                    val fromDate: Date = format.parse(key)
                    val fromWeek = DateUtils.getCurrentWeek(openingDate, fromDate, setting.firstDayOfWeek)
                    calendar.time = fromDate
                    val fromWeekday = calendar.get(Calendar.DAY_OF_WEEK)
                    val toDate: Date = format.parse(value)
                    val toWeek = DateUtils.getCurrentWeek(openingDate, toDate, setting.firstDayOfWeek)
                    calendar.time = toDate
                    val toWeekday = calendar.get(Calendar.DAY_OF_WEEK)
                    val toPair = Pair(toWeek, toWeekday)
                    fromToMap.getOrPut(fromWeek, { HashMap() })[fromWeekday] = toPair
                }
                //添加放假日期
                if (holiday.day != 0) {
                    val holidayDate: Date = format.parse(holiday.date)
                    calendar.time = holidayDate
                    for (i in 0 until holiday.day) {
                        val holidayWeek = DateUtils.getCurrentWeek(openingDate, calendar.time, setting.firstDayOfWeek)
                        if (holidayWeek <= setting.weekCnt) {
                            fromToMap.getOrPut(holidayWeek, { HashMap() }).run {
                                val weekday = calendar.get(Calendar.DAY_OF_WEEK)
                                if (!this.containsKey(weekday)) {
                                    this[weekday] = null
                                }
                            }
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }
                }
            }
            fromToMap
        }


        /**
         * 节假日调课，并按周排列课表
         * @return MutableList<MutableList<CourseBase>?> 分周排列课表
         * @throws ParseException
         */
        suspend fun holidayChange(oldCourses: List<Course>): MutableList<MutableList<ScheduleItem>?> = withContext(Dispatchers.Default) {
            //MutableMap<fromWeek, MutableMap<fromWeekday, Pair<toWeek, toWeekday>>>
            val fromTo = syllabus.getAdjustmentInfo()
            //按周排列课程
            val courseArray: MutableList<MutableList<ScheduleItem>?> = ArrayList()
            for (i in 0 until 24) {
                courseArray.add(null)
            }
            oldCourses.forEach { course ->
                course.weekSet.forEach { week ->
                    var flag = true
                    if (fromTo.containsKey(week)) {
                        val h = fromTo[week] ?: error("")
                        for ((weekday, weekAndWeekday) in h) {
                            if (weekday == course.weekday) {
                                if (weekAndWeekday != null) {
                                    val c = course.toCourseItem()
                                    c.address += "\n[补课]"
                                    c.dayOfWeek = weekAndWeekday.second
                                    if (courseArray[week - 1] == null) {
                                        courseArray[week - 1] = ArrayList()
                                    }
                                    courseArray[week - 1]!!.add(c)
                                }
                                flag = false
                                break
                            }
                        }
                    }
                    if (flag) {
                        if (courseArray[week - 1] == null) {
                            courseArray[week - 1] = ArrayList()
                        }
                        courseArray[week - 1]!!.add(course.toCourseItem())
                    }
                }
            }
            courseArray
        }

        /**
         * 获取调课后的课
         */
        suspend fun adjustCourse(courses: List<Course>) = withContext(Dispatchers.Default) {
            val courseMutableList = courses.toMutableList()
            val holiday = getAdjustmentInfo()
            for (course in courseMutableList) {
                for (week in course.weekSet) {
                    (holiday[week] ?: continue)[course.weekday].run {
                        course.weekSet.remove(week)
                        if (this != null) {
                            val courseClone = course.clone()
                            courseClone.weekSet.clear()
                            courseClone.weekSet.add(this.first)
                            courseClone.weekday = this.second
                            courseMutableList.add(course)
                        }
                    }
                }
            }
            courseMutableList
        }

        suspend fun saveSetting(syllabusSetting: SyllabusSetting) = withContext(Dispatchers.Default) {
            settingDao.save(syllabusSetting)
        }

        suspend fun getAll(): List<Course> {
            return withContext(Dispatchers.Default) {
                dao.getAll(account)
            }.ifEmpty { fetchAll().data ?: emptyList() }
        }

        suspend fun delete(courses: List<Course>) = withContext(Dispatchers.Default) {
            dao.delete(*courses.toTypedArray())
        }

        suspend fun delete(course: Course) = withContext(Dispatchers.Default) {
            dao.delete(course)
        }

        private suspend fun deleteNetwork() = withContext(Dispatchers.Default) {
            dao.delete(account, false)
        }
    }

    override suspend fun checkoutTo(user: User) = withContext(Dispatchers.IO) {
        jwService.checkoutTo(user)
        user.account = user.account
        account = user.account
        RepositoryImpl.user.saveLoginOnly(user)
    }

    override suspend fun getNewVersion(): IFResult<Version> = withContext(Dispatchers.IO) {
        try {
            woService.getNewVersion()
        } catch (e: Exception) {
            IFResult.failure<Version>(e)
        }
    }

    override suspend fun getOpeningDay(): IFResult<String> = withContext(Dispatchers.IO) {
        try {
            woService.getOpeningDay()
        } catch (e: Exception) {
            IFResult.failure<String>(e.errorMessage())
        }
    }

    override suspend fun postFeedback(message: String, contact: String): IFResult<String> = withContext(Dispatchers.IO) {
        try {
            val resp = HttpClient().post(url = "http://woolsen.cn/feedback", body = mapOf(
                    "sno" to user.getInUseAccount(),
                    "contact" to contact,
                    "message" to message
            ))
            if (resp.isSuccessful && resp.body() != null) {
                val jo = JSONObject.parseObject(resp.body()?.string())
                if (jo.getIntValue("code") == 200) {
                    IFResult.success("感谢小伙伴的反馈~\niFAFU会第一时间处理")
                } else {
                    IFResult.failure<String>(jo.getString("message"))
                }
            } else {
                IFResult.failure<String>("反馈提交出错，请加QQ群反馈")
            }
        } catch (e: Exception) {
            IFResult.failure<String>(e)
        }
    }


    class UserRt(context: Context) {

        private val userDao = AppDatabase.getInstance(context).userDao

        fun getInUseAccount(): String {
            return account
        }

        suspend fun login(user: User): Response<String> {
            return login(user.account, user.password)
        }

        suspend fun login(account: String, password: String): Response<String> = withContext(Dispatchers.IO) {
            val user = User().apply {
                this.account = if (account.getOrNull(0) == '0') account.drop(1) else account
                this.password = password
                if (account.length == 9) {
                    this.school = User.FAFU_JS
                } else if (account.length == 10) {
                    this.school = User.FAFU
                }
            }
            val loginUrl = Constant.getUrl(ZFApiList.LOGIN, user)
            val verifyUrl = Constant.getUrl(ZFApiList.VERIFY, user)
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
                                    .get(verifyUrl)
                                    .execute())
                    params["txtSecretCode"] = verifyCode
                    return@withContext loginParser.parse(
                            APIManager.zhengFangAPI
                                    .login(loginUrl, params)
                                    .execute().body()!!
                                    .string()
                    )
                } catch (e: VerifyException) {
                    continue
                } catch (e: IOException) {
                    return@withContext Response.error<String>("教务管理系统又崩溃了")
                }
            }
            return@withContext Response.error<String>("登录出错")
        }

        suspend fun getInUse(): User? = withContext(Dispatchers.Default) {
            var user = db.userDao.user(account)
            if (user == null) {
                user = userDao.allUser().getOrNull(0)
                if (user != null) {
                    saveLoginOnly(user)
                }
            }
            user
        }

        suspend fun getAll(): List<User> = withContext(Dispatchers.Default) {
            db.userDao.allUser()
        }

        suspend fun save(user: User) = withContext(Dispatchers.Default) {
            db.userDao.save(user)
        }

        suspend fun saveLoginOnly(user: User) = withContext(Dispatchers.Default) {
            account = user.account
            SPUtils[Constant.SP_USER_INFO].putString("account", user.account)
        }

        suspend fun delete(account: String) = withContext(Dispatchers.Default) {
            db.userDao.delete(account)
            db.courseDao.delete(account)
            db.examDao.delete(account)
            db.scoreDao.deleteScore(account)
            db.globalSettingDao.delete(account)
            db.syllabusSettingDao.delete(account)
            db.elecQueryDao.delete(account)
            db.elecUserDao.delete(account)
            db.elecCookieDao.delete(account)
            db.electivesDao.delete(account)
        }

    }

    object XfbRt {
        suspend fun elecCardBalance(): Response<Double> = withContext(Dispatchers.IO) {
            try {
                val responseBody =
                        APIManager.xfbAPI.queryBalance("true").execute().body()
                val body = mapOf("json" to "true")

                val msg = JSONObject.parseObject(responseBody!!.string())
                        .getJSONObject("Msg")
                        .getJSONObject("query_card")
                        .getJSONArray("card")
                        .getJSONObject(0)
                val value = (msg.getIntValue("db_balance") + msg.getIntValue("unsettle_amount")) / 100.0
                Response.success(value)
            } catch (e: Exception) {
                e.printStackTrace()
                Response.error<Double>(msg = "校园卡余额获取出错")
            }
        }

        suspend fun checkLoginStatus(): Boolean = withContext(Dispatchers.IO) {
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

        suspend fun fetchElectricityInfo(query: ElecQuery): Response<String> = withContext(Dispatchers.IO) {
            val responseBody = APIManager.xfbAPI.query(
                    query.toFiledMap(ElecQuery.Query.ROOMINFO)
            ).execute().body()
            try {
                Response.success(JSONObject.parseObject(responseBody!!.string())
                        .getJSONObject("Msg")
                        .getJSONObject("query_elec_roominfo")
                        .getString("errmsg"))
            } catch (e: Exception) {
                Response.error<String>("查询出错")
            }
        }

        fun getSelectionList(): List<ElecSelection> {
            val jsonString = context.assets.open("room_info.json").reader().readText()
            return JSONArray.parseArray(jsonString).map {
                val jo = JSONObject.parseObject(it.toString())
                ElecSelection(
                        aid = jo["aid"]?.toString() ?: "",
                        name = jo["name"]?.toString() ?: "",
                        areaId = jo["areaId"]?.toString() ?: "",
                        area = jo["area"]?.toString() ?: "",
                        buildingId = jo["buildingId"]?.toString() ?: "",
                        building = jo["building"]?.toString() ?: "",
                        floorId = jo["floorId"]?.toString() ?: "",
                        floor = jo["floor"]?.toString() ?: "",
                        group1 = jo["group1"]?.toString() ?: "",
                        group2 = jo["group2"]?.toString() ?: ""
                )
            }
        }

        /**
         * 电费查询
         */
        fun getElecQuery(): ElecQuery? {
            return db.elecQueryDao.elecQuery(account)
        }

        fun saveElecQuery(elecQuery: ElecQuery) {
            db.elecQueryDao.save(elecQuery)
        }

        fun getElecCookie(): ElecCookie {
            var cookie = db.elecCookieDao.elecCookie(account)
            if (cookie == null) {
                cookie = ElecCookie()
                cookie.account = account
                saveElecCookie(cookie)
            }
            return cookie
        }

        fun saveElecCookie(cookie: ElecCookie) {
            db.elecCookieDao.save(cookie)
        }

        fun getElecUser(): ElecUser? {
            return db.elecUserDao.elecUser(account)
        }

        fun saveElecUser(elecUser: ElecUser) {
            db.elecUserDao.save(elecUser)
        }

        suspend fun elecCookieInit() {
            withContext(Dispatchers.IO) {
                val service = APIManager.xfbAPI
                service.init("0", SPUtils[Constant.SP_ELEC].getString("IMEI"), "0").execute()
            }
        }

        suspend fun elecLogin(account: String, password: String, verify: String): String = withContext(Dispatchers.IO) {
            Timber.d("电费查询   account:$account  password:$password   verify:$verify")
            val service = APIManager.xfbAPI
            service.login(
                    "http://cardapp.fafu.edu.cn:8088/Phone/Login?sourcetype=0&IMEI=" +
                            SPUtils[Constant.SP_ELEC].getString("IMEI") + "&language=0",
                    account, String(Base64.encode(password.toByteArray(), Base64.DEFAULT)),
                    verify, "1", "1", "", "true"
            ).execute().body()!!.string()
        }

        suspend fun elecVerifyBitmap(): Bitmap = withContext(Dispatchers.IO) {
            val service = APIManager.xfbAPI
            service.init("0", SPUtils.get(Constant.SP_ELEC).getString("IMEI"), "0").execute()
            val responseBody = service.verify(System.currentTimeMillis().toString()).execute().body()
            val bytes = responseBody!!.bytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }

    }

    object ElectivesRt {

        suspend fun fetch(): Electives = withContext(Dispatchers.IO) {
            val user = user.getInUse() ?: throw Exception("用户信息不存在")
            val queryUrl = Constant.getUrl(ZFApiList.ELECTIVES, user)
            val mainUrl = Constant.getUrl(ZFApiList.MAIN, user)
            val html = APIManager.zhengFangAPI
                    .get(queryUrl, mainUrl).execute()
                    .body()!!.string()
            ElectivesParser(user).parse(html)
        }

        suspend fun get(): Electives? = withContext(Dispatchers.Default) {
            db.electivesDao.electives(account)
        }

        suspend fun save(electives: Electives) = withContext(Dispatchers.Default) {
            db.electivesDao.save(electives)
        }
    }

    object ScoreRt {

        /**
         * 获取成绩信息后自动保存
         */
        suspend fun fetchAll(): Response<List<Score>> = withContext(Dispatchers.IO) {
            val user = user.getInUse() ?: throw Exception("用户信息不存在")
            val scoreUrl = Constant.getUrl(ZFApiList.SCORE, user)
            val mainUrl = Constant.getUrl(ZFApiList.MAIN, user)
            val params = fetchParams(scoreUrl, mainUrl)
            params["ddlxn"] = "全部"
            params["ddlxq"] = "全部"
            params["btnCx"] = ""
            val html = APIManager.zhengFangAPI
                    .post(scoreUrl, scoreUrl, params).execute()
                    .body()!!.string()
            val parser = ScoreParser(user)
            parser.parse(html).apply {
                val list = data
                //若获取的成绩不为空，则清空数据库成绩并保存刚获取的成绩
                if (list != null && list.isNotEmpty()) {
                    db.scoreDao.deleteScore(account)
                    db.scoreDao.saveScore(list)
                }
            }
        }

        suspend fun fetchAll(year: String, term: String): Response<List<Score>> = withContext(Dispatchers.IO) {
            val all = fetchAll()
            all.copy(data = all.data?.run {
                if (year != "全部" && term != "全部") {
                    filter { it.term == term && it.year == year }
                } else if (year != "全部") {
                    filter { it.year == year }
                } else if (term != "全部") {
                    filter { it.term == term }
                } else {
                    this
                }
            })
        }

        suspend fun fetchNow() = withContext(Dispatchers.IO) {
            val semester = getNowSemester()
            fetchAll(semester.yearStr, semester.termStr)
        }

        fun getNow(): LiveData<List<Score>> {
            val semester = getNowSemester()
            return db.scoreDao.getAllScores(account, semester.yearStr, semester.termStr)
        }


    }

    class ExamRt {

        suspend fun getNow(): List<Exam> = withContext(Dispatchers.Default) {
            val semester = getNowSemester()
            db.examDao.getAll(account, semester.yearStr, semester.termStr)
        }

        suspend fun getAll(): List<Exam> = withContext(Dispatchers.Default) {
            db.examDao.getAll(account)
        }

        suspend fun getAll(year: String, term: String): List<Exam> = withContext(Dispatchers.Default) {
            if (year == "全部" && term == "全部") {
                db.examDao.getAll(account)
            } else if (year == "全部") {
                db.examDao.getAllByTerm(account, term)
            } else if (term == "全部") {
                db.examDao.getAllByYear(account, year)
            } else {
                db.examDao.getAll(account, year, term)
            }
        }

        suspend fun save(exams: List<Exam>) = withContext(Dispatchers.Default) {
            db.examDao.save(*exams.toTypedArray())
        }
    }

    suspend fun fetch(year: String, term: String): Response<List<Exam>> = withContext(Dispatchers.IO) {
        val user = user.getInUse() ?: throw IllegalAccessException("用户信息不存在")
        val examUrl = Constant.getUrl(ZFApiList.EXAM, user)
        val mainUrl = Constant.getUrl(ZFApiList.MAIN, user)
        val params = fetchParams(examUrl, mainUrl).apply {
            put("xnd", year.encode("gb2312"))
            put("xqd", term.encode("gb2312"))
            put("btnCx", " 查  询 ".encode("gb2312"))
        }
        val html = APIManager.zhengFangAPI
                .post(examUrl, mainUrl, params)
                .execute().body()!!.string()
        ExamParser(user).parse(html).apply {
            if (!data.isNullOrEmpty()) {
                exam.save(data)
            }
        }
    }

    override suspend fun getNotExamsFromDbOrNet(): IFResult<List<Exam>> {
        return jwService.getNowExams()
    }

    override suspend fun getExamsFromDbOrNet(year: String, term: String): IFResult<List<Exam>> {
        val dbDate = if (year == "全部" && term == "全部") {
            db.examDao.getAll(account)
        } else if (year == "全部") {
            db.examDao.getAllByTerm(account, term)
        } else if (term == "全部") {
            db.examDao.getAllByYear(account, year)
        } else {
            db.examDao.getAll(account, year, term)
        }
        return if (dbDate.isNotEmpty()) {
            IFResult.success<List<Exam>>(dbDate)
        } else {
            try {
                val resp = fetch(year, term)
                if (resp.isSuccess && resp.data != null) {
                    IFResult.success(resp.data)
                } else {
                    IFResult.failure(resp.message)
                }
            } catch (e: Exception) {
                IFResult.failure<List<Exam>>(e)
            }
        }
    }

    object GlobalSettingRt {

        suspend fun get(): GlobalSetting = withContext(Dispatchers.Default) {
            db.globalSettingDao.globalSetting(account) ?: GlobalSetting(account).apply {
                save(this)
            }
        }

        suspend fun save(setting: GlobalSetting) = withContext(Dispatchers.Default) {
            db.globalSettingDao.save(setting)
        }

    }


    override suspend fun getWeather(code: String): IFResult<Weather> = withContext(Dispatchers.IO) {
        try {
            woService.getWeather(code)
        } catch (e: Exception) {
            IFResult.failure<Weather>(e.errorMessage())
        }
    }

    private fun Throwable.errorMessage(): String {
        return when (this) {
            is UnknownHostException, is ConnectException ->
                "网络错误，请检查网络设置"
            is SocketTimeoutException ->
                "服务器连接超时（可能原因：学校服务器崩溃）"
            is SQLiteConstraintException ->
                "数据库数据错误（错误信息：${message}）"
            is IOException ->
                if (this.message?.contains("unexpected") == true) {
                    "正方教务系统又崩溃了！"
                } else {
                    message ?: "Net Error"
                }
            else ->
                message ?: "ERROR"
        }
    }
}