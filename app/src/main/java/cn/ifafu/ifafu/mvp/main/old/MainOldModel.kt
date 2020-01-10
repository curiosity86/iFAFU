package cn.ifafu.ifafu.mvp.main.old

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.entity.NextExam
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.entity.SyllabusSetting
import cn.ifafu.ifafu.entity.YearTerm
import cn.ifafu.ifafu.mvp.main.BaseMainModel
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainOldModel(context: Context) : BaseMainModel(context), MainOldContract.Model {
    private lateinit var toYear: String
    private lateinit var toTerm: String

    override fun getSyllabusSetting(): SyllabusSetting {
        return mRepository.getSyllabusSetting()
    }

    override fun getFunctionTab(): Map<String, List<Pair<String, Int>>> {
        return mapOf(
                "信息查询" to listOf(
                        Pair("成绩查询", R.drawable.menu_score_white),
                        Pair("学生考试查询", R.drawable.menu_exam_white),
                        Pair("电费查询", R.drawable.menu_elec_white),
                        Pair("选修学分查询", R.drawable.menu_elective_white)),
                "实用工具" to listOf(
                        Pair("我的课表", R.drawable.menu_syllabus_white),
                        Pair("网页模式", R.drawable.menu_web_white),
                        Pair("报修服务", R.drawable.main_old_tabs_repair),
                        Pair("一键评教", R.drawable.main_old_tabs_comment)),
                "软件设置" to listOf(
                        Pair("软件设置", R.drawable.menu_setting_white),
                        Pair("账号管理", R.drawable.main_old_tabs_manage)),
                "关于软件" to listOf(
                        Pair("检查更新", R.drawable.menu_update_white),
                        Pair("关于iFAFU", R.drawable.main_old_tabs_about)))
    }


    @SuppressLint("DefaultLocale")
    override fun getYearTermList(): Observable<YearTerm> {
        return Observable.fromCallable { mRepository.getNowYearTerm() }
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
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.CHINA)
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

    override suspend fun getScore(): List<Score> = withContext(Dispatchers.IO) {
        val yearTerm = mRepository.getNowYearTerm()
        val scoreResponse = mRepository.fetchScoreList()
        val score = scoreResponse.body!!
        if (score.isEmpty()) {
            mRepository.getScores(yearTerm.termStr, yearTerm.yearStr)
        } else {
            score.filter { it.term == yearTerm.termStr && it.year == yearTerm.yearStr }
        }
    }
}