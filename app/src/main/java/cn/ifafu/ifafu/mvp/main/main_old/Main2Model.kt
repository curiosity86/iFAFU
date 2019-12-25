package cn.ifafu.ifafu.mvp.main.main_old

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.entity.*
import cn.ifafu.ifafu.mvp.main.BaseMainModel
import cn.ifafu.ifafu.mvp.score_list.ScoreListModel
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*

class Main2Model(context: Context) : BaseMainModel(context), Main2Contract.Model {
    private lateinit var toYear: String
    private lateinit var toTerm: String

    override fun saveLoginUser(user: User) {
        repository.saveUser(user)
    }

    override fun getSyllabusSetting(): SyllabusSetting {
        return repository.getSyllabusSetting()!!
    }

    override fun getFunctionTab(): Map<String, List<Pair<String, Int>>> {
        return mapOf(
                "信息查询" to listOf(
                        Pair("成绩查询", R.drawable.main_old_tabs_score),
                        Pair("学生考试查询", R.drawable.main_old_tabs_exam),
                        Pair("电费查询", R.drawable.main_old_tabs_elec)),
                "实用工具" to listOf(
                        Pair("我的课表", R.drawable.main_old_tabs_syllabus),
                        Pair("网页模式", R.drawable.main_old_tabs_web),
                        Pair("报修服务", R.drawable.main_old_tabs_repair),
                        Pair("一键评教", R.drawable.main_old_tabs_comment)),
                "软件设置" to listOf(
                        Pair("软件设置", R.drawable.main_old_tabs_setting),
                        Pair("账号管理", R.drawable.main_old_tabs_manage)),
                "关于软件" to listOf(
                        Pair("检查更新", R.drawable.main_old_tabs_update),
                        Pair("关于iFAFU", R.drawable.main_old_tabs_about)))
    }


    @SuppressLint("DefaultLocale")
    override fun getYearTermList(): Observable<YearTerm> {
        return Observable.fromCallable { repository.getYearTermList() }
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
                    val thisTermExams = getThisTermExams()
                            .filter { it.startTime > now || it.startTime == 0L }
                    val list = ArrayList<NextExam>()
                    val max = if (thisTermExams.size < 2) thisTermExams.size else 2
                    val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
                    val timeFormat = SimpleDateFormat("hh:mm", Locale.CHINA)
                    for (i in 0 until max) {
                        var time: String
                        var last: Pair<String, String>
                        if (thisTermExams[i].startTime == 0L) {
                            time = "暂无考试时间"
                            last = Pair("", "")
                        } else {
                            time = dateFormat.format(Date(thisTermExams[i].startTime)) +
                                    "(${timeFormat.format(Date(thisTermExams[i].startTime))}" +
                                    "-" +
                                    "${timeFormat.format(Date(thisTermExams[i].endTime))})"
                            last = calcExamIntervalTime(now, thisTermExams[i].startTime)
                        }
                        list.add(NextExam(
                                name = thisTermExams[i].name,
                                time = time,
                                address = thisTermExams[i].address,
                                seatNum = thisTermExams[i].seatNumber,
                                last = last
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
        val scoreModel = ScoreListModel(mContext)
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