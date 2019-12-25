package cn.ifafu.ifafu.data

import cn.ifafu.ifafu.entity.*

interface Repository {

    fun getInUseUser(): User?
    fun getAllUsers(): List<User>
    fun saveUser(user: User)
    fun saveLoginUser(user: User)
    fun deleteUser(user: User)

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

    fun getAllScores(): List<Score>
    fun getScoresByYear(year: String): List<Score>
    fun getScoresByTerm(term: String): List<Score>
    fun getScoreById(id: Long): Score?
    fun getScores(year: String, term: String): List<Score>
    fun deleteScore(year: String, term: String)
    fun deleteAllScore()
    fun saveScore(score: Score)
    fun saveScore(scores: List<Score>)

    fun getAllExams(): List<Exam>
    fun getExams(year: String, term: String): List<Exam>
    fun saveExam(exams: List<Exam>)
    fun getToken(account: String): Token?
    fun saveToken(token: Token)

    fun getYearTermList(): YearTerm
    fun getYearTerm(): Pair<String, String>

    //电费查询
    fun getElecQuery(): ElecQuery?
    fun saveElecQuery(elecQuery: ElecQuery)
    fun getElecCookie(): ElecCookie?
    fun saveElecCookie(cookie: ElecCookie)
    fun getElecUser(): ElecUser?
    fun saveElecUser(elecUser: ElecUser)

    fun getGlobalSetting(): GlobalSetting
    fun saveGlobalSetting(setting: GlobalSetting)

    fun clearAllData()
}