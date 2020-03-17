package cn.ifafu.ifafu.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.bean.*
import cn.ifafu.ifafu.data.db.AppDatabase
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.data.exception.VerifyException
import cn.ifafu.ifafu.data.retrofit.APIManager
import cn.ifafu.ifafu.data.retrofit.parser.*
import cn.ifafu.ifafu.data.retrofit.service.WeatherService
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.util.HttpUtils
import cn.ifafu.ifafu.util.SPUtils
import cn.ifafu.ifafu.util.encode
import cn.ifafu.ifafu.view.syllabus.CourseBase
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Repository {

    private lateinit var context: Context

    private val db: AppDatabase by lazy { AppDatabase.getInstance(context) }
    val user by lazy { UserRt(context) }
    val syllabus by lazy { SyllabusRt(context) }
    val exam by lazy { ExamRt(context) }

    private var account: String = ""
        get() {
            return field.ifEmpty {
                field = SPUtils.get(Constant.SP_USER_INFO).getString("account")
                field
            }
        }

    /**
     * 启动App时初始化
     */
    fun init(context: Context) {
        Repository.context = context
    }

    private suspend fun fetchParams(url: String): MutableMap<String, String> = withContext(Dispatchers.IO) {
        val responseBody = APIManager.zhengFangAPI.initParams2(url).execute().body()!!
        val paramsParser = ParamsParser()
        paramsParser.parse(responseBody.string())
    }

    private suspend fun fetchParams(url: String, referer: String): MutableMap<String, String> = withContext(Dispatchers.IO) {
        val response = APIManager.zhengFangAPI.initParams2(url, referer).execute()
        val paramsParser = ParamsParser2()
        paramsParser.parse(response)
    }

    fun getNowSemester(): Semester {
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
        yearList.add("全部")
        return Semester(yearList, arrayListOf("1", "2", "全部"), 0, termIndex)
    }

    class SyllabusRt(context: Context) {

        private val dao = AppDatabase.getInstance(context).courseDao
        private val settingDao = AppDatabase.getInstance(context).syllabusSettingDao

        suspend fun getOpeningDay(): String = withContext(Dispatchers.IO) {
            val json = JSONObject.parseObject(HttpUtils.get("https://api.ifafu.cn/public/text/firstWeek").body()?.string())
            val content = json["content"].toString().substring(0, 10)
            kotlin.runCatching {
                SimpleDateFormat("yyyy-MM-dd").parse(content) //确保符合格式
            }
            content
        }

        suspend fun fetchAll(): Response<List<Course>> = withContext(Dispatchers.IO) {
            val user = user.getInUse() ?: throw Exception("用户信息不存在")
            val url: String = School.getUrl(ZFApiList.SYLLABUS, user)
            val headerReferer: String = School.getUrl(ZFApiList.MAIN, user)
            val html = APIManager.zhengFangAPI
                    .getInfo2(url, headerReferer).execute()
                    .body()!!.string()
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
                        val isJS = data.find { it.address.contains("旗教") } == null
                        val times = ArrayList<Int>()
                        Collections.addAll(times, *SyllabusSetting.intBeginTime[if (isJS) 1 else 0])
                        beginTime = times
                    })
                }
            }
        }

        suspend fun get(id: Long): Course? = withContext(Dispatchers.Default) {
            dao.course(id)
        }

        suspend fun save(course: Course) = withContext(Dispatchers.Default) {
            dao.save(course)
        }

        suspend fun save(courses: List<Course>) = withContext(Dispatchers.Default) {
            dao.save(*courses.toTypedArray())
        }

        suspend fun getSetting(): SyllabusSetting = withContext(Dispatchers.Default) {
            settingDao.syllabusSetting(account) ?: SyllabusSetting(account).apply {
                //若教室存在旗教字样，则为旗山校区
                val isJS = getAll().find { it.address.contains("旗教") } == null
                val times = ArrayList<Int>()
                Collections.addAll(times, *SyllabusSetting.intBeginTime[if (isJS) 1 else 0])
                beginTime = times
            }
        }

        suspend fun getHoliday() = withContext(Dispatchers.Default) {
            listOf(
                    Holiday("清明节", "2020-04-04", 3),
                    Holiday("劳动节", "2020-05-01", 5).apply {
                        addFromTo("2020-05-05", "2019-05-09")
                    },
                    Holiday("端午节", "2020-06-25", 3).apply {
                        addFromTo("2020-06-26", "2019-06-28")
                    }
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
                if (holiday.fromTo != null) { //节假日需要调课
                    for ((key, value) in holiday.fromTo) {
                        val fromDate: Date = format.parse(key)
                        val fromWeek = DateUtils.getCurrentWeek(openingDate, fromDate, setting.firstDayOfWeek)
                        calendar.time = fromDate
                        val fromWeekday = calendar.get(Calendar.DAY_OF_WEEK)
//                    Log.d("Holiday Calc", "from week: $fromWeek, fromWeekday: $fromWeekday")
                        val toDate: Date = format.parse(value)
                        val toWeek = DateUtils.getCurrentWeek(openingDate, toDate, setting.firstDayOfWeek)
                        calendar.time = toDate
                        val toWeekday = calendar.get(Calendar.DAY_OF_WEEK)
                        val toPair = Pair(toWeek, toWeekday)
                        fromToMap.getOrPut(fromWeek, { HashMap() })[fromWeekday] = toPair
                    }
                }
                //添加放假日期
                if (holiday.day != 0) {
                    val holidayDate: Date = format.parse(holiday.date)
                    calendar.time = holidayDate
//                Log.d("Holiday Calc", "holiday date: ${holiday.date}    day: ${holiday.day}天")
                    for (i in 0 until holiday.day) {
                        val holidayWeek = DateUtils.getCurrentWeek(openingDate, calendar.time, setting.firstDayOfWeek)
//                    Log.d("Holiday Calc", "holiday week = $holidayWeek    $i")
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
        suspend fun holidayChange(oldCourses: List<Course>): MutableList<MutableList<CourseBase>?> = withContext(Dispatchers.Default) {
            //MutableMap<fromWeek, MutableMap<fromWeekday, Pair<toWeek, toWeekday>>>
            val fromTo = syllabus.getAdjustmentInfo()
            //按周排列课程
            val courseArray: MutableList<MutableList<CourseBase>?> = ArrayList()
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
                                    val c = course.toCourseBase()
                                    c.text += "\n[补课]"
                                    c.weekday = weekAndWeekday.second
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
                        courseArray[week - 1]!!.add(course.toCourseBase())
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
            db.scoreDao.delete(account)
            db.globalSettingDao.delete(account)
            db.syllabusSettingDao.delete(account)
            db.tokenDao.delete(account)
            db.elecQueryDao.delete(account)
            db.elecUserDao.delete(account)
            db.elecCookieDao.delete(account)
            db.electivesDao.delete(account)
        }

    }

    object TokenRt {

        suspend fun get(account: String): Token? = withContext(Dispatchers.Default) {
            db.tokenDao.token(account)
        }

        suspend fun save(token: Token) = withContext(Dispatchers.Default) {
            db.tokenDao.save(token)
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
                service.init("0", SPUtils.get(Constant.SP_ELEC).getString("IMEI"), "0").execute()
            }
        }

        suspend fun elecLogin(account: String, password: String, verify: String): String = withContext(Dispatchers.IO) {
            Log.d("电费查询", "account:$account   password:$password   verify:$verify")
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
            val queryUrl = School.getUrl(ZFApiList.ELECTIVES, user)
            val mainUrl = School.getUrl(ZFApiList.MAIN, user)
            val html = APIManager.zhengFangAPI
                    .getInfo2(queryUrl, mainUrl).execute()
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
            val scoreUrl = School.getUrl(ZFApiList.SCORE, user)
            val mainUrl = School.getUrl(ZFApiList.MAIN, user)
            val params = fetchParams(scoreUrl, mainUrl)
            params["ddlxn"] = "全部"
            params["ddlxq"] = "全部"
            params["btnCx"] = ""
            val html = APIManager.zhengFangAPI
                    .getInfo2(scoreUrl, scoreUrl, params).execute()
                    .body()!!.string()
            val parser = ScoreParser(user)
            parser.parse(html).apply {
                val list = data
                //若获取的成绩不为空，则清空数据库成绩并保存刚获取的成绩
                if (list != null && list.isNotEmpty()) {
                    deleteAll()
                    save(list)
                    //筛选不需要计入智育分的成绩
                    val scoreFilter = getFilter()
                    scoreFilter.account = account
                    scoreFilter.filter(list)
                    saveFilter(scoreFilter)
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

        suspend fun getAll(): List<Score> = withContext(Dispatchers.Default) {
            db.scoreDao.getAll(account)
        }

        suspend fun getNow(): List<Score> = withContext(Dispatchers.Default) {
            val semester = getNowSemester()
            db.scoreDao.getAll(account, semester.yearStr, semester.termStr)
        }

        suspend fun getById(id: Long): Score? = withContext(Dispatchers.Default) {
            db.scoreDao.getScoreById(id)
        }

        suspend fun getAll(year: String, term: String): List<Score> = withContext(Dispatchers.Default) {
            if (year == "全部" && term == "全部") {
                db.scoreDao.getAll(account)
            } else if (year == "全部") {
                db.scoreDao.getAllByTerm(account, term)
            } else if (term == "全部") {
                db.scoreDao.getAllByYear(account, year)
            } else {
                db.scoreDao.getAll(account, year, term)
            }
        }

        suspend fun delete(year: String, term: String) = withContext(Dispatchers.Default) {
            db.scoreDao.delete(year, term)
        }

        suspend fun deleteAll() = withContext(Dispatchers.Default) {
            db.scoreDao.delete(account)
        }

        suspend fun save(score: Score) = withContext(Dispatchers.Default) {
            db.scoreDao.save(score)
        }

        suspend fun save(scores: List<Score>) = withContext(Dispatchers.Default) {
            db.scoreDao.save(*scores.toTypedArray())
        }

        suspend fun getFilter(): ScoreFilter = withContext(Dispatchers.Default) {
            val account = account
            db.scoreFilterDao.scoreFilter(account) ?: ScoreFilter().apply {
                this.account = account
            }
        }

        suspend fun saveFilter(scoreFilter: ScoreFilter) = withContext(Dispatchers.Default) {
            db.scoreFilterDao.save(scoreFilter)
        }
    }

    class ExamRt(context: Context) {

        /**
         * 获取当前学期考试（速度最快）
         */
        suspend fun fetchNow(): Response<List<Exam>> = withContext(Dispatchers.IO) {
            val user = user.getInUse()!!
            val examUrl = School.getUrl(ZFApiList.EXAM, user)
            val mainUrl = School.getUrl(ZFApiList.MAIN, user)
            val html = APIManager.zhengFangAPI
                    .getInfo2(examUrl, mainUrl)
                    .execute().body()!!.string()
            ExamParser(user).parse(html).apply {
                if (!data.isNullOrEmpty()) {
                    save(data)
                }
            }
        }

        suspend fun fetch(year: String, term: String): Response<List<Exam>> = withContext(Dispatchers.IO) {
            val user = user.getInUse()!!
            val examUrl = School.getUrl(ZFApiList.EXAM, user)
            val mainUrl = School.getUrl(ZFApiList.MAIN, user)
            val params = fetchParams(examUrl, mainUrl).apply {
                put("xnd", year.encode("gb2312"))
                put("xqd", term.encode("gb2312"))
                put("btnCx", " 查  询 ".encode("gb2312"))
            }
            val html = APIManager.zhengFangAPI
                    .getInfo2(examUrl, mainUrl, params)
                    .execute().body()!!.string()
            ExamParser(user).parse(html).apply {
                if (!data.isNullOrEmpty()) {
                    save(data)
                }
            }
        }

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

    object WeatherRt {

        /**
         * 101230101:福州
         */
        suspend fun fetch(cityCode: String): Response<Weather> = withContext(Dispatchers.IO) {
            val weather = Weather()
            val referer = "http://www.weather.com.cn/weather1d/$cityCode.shtml"
            val service: WeatherService = APIManager.weatherAPI

            // 获取城市名和当前温度
            val url1 = "http://d1.weather.com.cn/sk_2d/$cityCode.html"
            val body1 = service.getWeather(url1, referer).execute().body()
            var jsonStr1: String = body1!!.string()
            jsonStr1 = jsonStr1.replace("var dataSK = ", "")
            val jo1: JSONObject = JSONObject.parseObject(jsonStr1)
            weather.cityName = jo1.getString("cityname")
            weather.nowTemp = jo1.getInteger("temp")
            weather.weather = jo1.getString("weather")

            // 获取白天温度和晚上温度
            val url2 = "http://d1.weather.com.cn/dingzhi/$cityCode.html"
            val body2 = service.getWeather(url2, referer).execute().body()
            var jsonStr2: String = body2!!.string()
            jsonStr2 = jsonStr2.substring(jsonStr2.indexOf('=') + 1, jsonStr2.indexOf(";"))
            var jo2: JSONObject = JSONObject.parseObject(jsonStr2)
            jo2 = jo2.getJSONObject("weatherinfo")
            weather.amTemp = Integer.valueOf(jo2.getString("temp").replace("℃", ""))
            weather.pmTemp = Integer.valueOf(jo2.getString("tempn").replace("℃", ""))
            Response.success(weather)
        }
    }


}