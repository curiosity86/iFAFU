package cn.ifafu.ifafu.data.local

import cn.ifafu.ifafu.entity.*

interface LocalDataSource {
    //用户信息查询
    fun getInUseUser(): User?
    fun getAllUsers(): List<User>
    fun saveUser(user: User)
    fun saveLoginUser(user: User)
    fun deleteUser(user: User)
    suspend fun deleteAccount(account: String)
    //课表查询
    fun getAllCourses(): List<Course>
    fun getCourses(local: Boolean): List<Course>
    fun getCourseById(id: Long): Course?
    fun saveCourse(course: Course)
    fun saveCourse(courses: List<Course>)
    fun deleteCourse(courses: List<Course>)
    fun deleteCourse(course: Course)
    fun deleteAllOnlineCourse()
    fun getSyllabusSetting(): SyllabusSetting
    fun saveSyllabusSetting(syllabusSetting: SyllabusSetting)
    //成绩查询
    fun getAllScores(): List<Score>
    fun getScoresByYear(year: String): List<Score>
    fun getScoresByTerm(term: String): List<Score>
    suspend fun getScoreById(id: Long): Score?
    fun getScores(year: String, term: String): List<Score>
    fun deleteScore(year: String, term: String)
    fun deleteAllScore()
    fun saveScore(score: Score)
    fun saveScore(scores: List<Score>)
    suspend fun getScoreFilter(): ScoreFilter
    suspend fun saveScoreFilter(scoreFilter: ScoreFilter)
    //考试查询
    fun getAllExams(): List<Exam>
    fun getExams(year: String, term: String): List<Exam>
    fun saveExam(exams: List<Exam>)
    fun getToken(account: String): Token?
    fun saveToken(token: Token)
    //学年学期查询
    fun getNowYearTerm(): YearTerm
    //电费查询
    fun getElecQuery(): ElecQuery?
    fun saveElecQuery(elecQuery: ElecQuery)
    fun getElecCookie(): ElecCookie?
    fun saveElecCookie(cookie: ElecCookie)
    fun getElecUser(): ElecUser?
    fun saveElecUser(elecUser: ElecUser)
    fun getSelectionList(): List<ElecSelection>
    //全局设置查询
    suspend fun getGlobalSetting(): GlobalSetting
    suspend fun saveGlobalSetting(setting: GlobalSetting)
    //选修学分要求
    suspend fun getElectives(): Electives?
    suspend fun saveElectives(electives: Electives)
}