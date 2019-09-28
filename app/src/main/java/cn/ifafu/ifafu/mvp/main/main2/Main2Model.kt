package cn.ifafu.ifafu.mvp.main.main2

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.*
import cn.ifafu.ifafu.mvp.main.BaseMainModel
import cn.ifafu.ifafu.mvp.score.ScoreModel
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*

class Main2Model(context: Context) : BaseMainModel(context), Main2Contract.Model {
    private lateinit var toYear: String
    private lateinit var toTerm: String

    override fun getAllUser(): MutableList<User> {
        return repository.allUser
    }

    override fun saveLoginUser(user: User) {
        repository.saveLoginUser(user)
    }

    override fun getSyllabusSetting(): SyllabusSetting {
        return repository.syllabusSetting
    }

    override fun getFunctionTab(): Map<String, List<Pair<String, Int>>> {
        return mapOf(
                "信息查询" to listOf(
                        Pair("成绩查询", R.drawable.tab_white_100),
                        Pair("学生考试查询", R.drawable.tab_white_exam),
                        Pair("电费查询", R.drawable.tab_white_elec)),
                "实用工具" to listOf(
                        Pair("我的课表", R.drawable.tab_white_syllabus),
                        Pair("网页模式", R.drawable.tab_white_web),
                        Pair("报修服务", R.drawable.tab_white_repair)),
                "软件设置" to listOf(
                        Pair("软件设置", R.drawable.ic_setting3),
                        Pair("账号管理", R.drawable.tab_white_manage)),
                "关于软件" to listOf(
                        Pair("检查更新", R.drawable.tab_white_update),
                        Pair("关于iFAFU", R.drawable.tab_white_about)))
    }


    @SuppressLint("DefaultLocale")
    override fun getYearTermList(): Observable<YearTerm> {
        return Observable.fromCallable { repository.yearTermList }
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTerm(): Pair<String, String> {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 6)
        HashMap<String, String>()
        toTerm = if (c.get(Calendar.MONTH) < 8) "1" else "2"
        val year = c.get(Calendar.YEAR)
        toYear = String.format("%d-%d", year - 1, year)
        return Pair(toYear, toTerm)
    }

    override fun getNextExams(): Observable<List<NextExam>> {
        return Observable
                .fromCallable {
                    val now = System.currentTimeMillis()
                    val list = getThisTermExams()
                            .filter { it.startTime > now }
                            .sortedBy { it.startTime }
                    list
                }
                .map {
                    val list = ArrayList<NextExam>()
                    val max = if (it.size < 2) it.size else 2
                    val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
                    val timeFormat = SimpleDateFormat("hh:mm", Locale.CHINA)
                    val now = System.currentTimeMillis()
                    for (i in 0 until max) {
                        val time = dateFormat.format(Date(it[i].startTime)) + "(" +
                                timeFormat.format(Date(it[i].startTime)) + "-" +
                                timeFormat.format(Date(it[i].endTime)) + ")"
                        list.add(NextExam(
                                name = it[i].name,
                                time = time,
                                address = it[i].address,
                                seatNum = it[i].seatNumber,
                                last = calcExamIntervalTime(now, it[i].startTime)
                        ))
                    }
                    list
                }
    }

    private fun calcExamIntervalTime(start: Long, end: Long): Pair<String, String> {
        val intervalSec = (end - start) / 1000
        return when {
            intervalSec >= (24 * 60 * 60) ->
                Pair("${intervalSec / (24 * 60 * 60)}", "天")
            intervalSec >= 60 * 60 ->
                Pair("${intervalSec / (60 * 60)}", "小时")
            else ->
                Pair("${intervalSec / 60}", "分钟")
        }
    }

    override fun getScore(): Observable<List<Score>> {
        val scoreModel = ScoreModel(mContext)
        val yearTerm: Pair<String, String> by lazy { scoreModel.getYearTerm().blockingFirst() }
        return Observable
                .fromCallable {
                    scoreModel.getScoresFromDB(yearTerm.first, yearTerm.second)
                }
                .flatMap { list ->
                    if (list.isEmpty()) {
                        scoreModel.getScoresFromNet(yearTerm.first, yearTerm.second)
                    } else {
                        Observable.just(list)
                    }
                }
    }
}