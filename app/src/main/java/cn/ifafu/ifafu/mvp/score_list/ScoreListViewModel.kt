package cn.ifafu.ifafu.mvp.score_list

import android.app.Application
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.entity.ScoreFilter
import cn.ifafu.ifafu.entity.YearTerm
import cn.ifafu.ifafu.util.ifFalse
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ScoreListViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var yearTerm: YearTerm

    private var scoreList: List<Score> = ArrayList()

    private lateinit var scoreFilter: ScoreFilter

    fun initOptionPickerData(success: suspend (yearList: List<String>,
                                               termList: List<String>,
                                               yearIndex: Int,
                                               termIndex: Int,
                                               title: String) -> Unit) {
        GlobalScope.launch {
            yearTerm = mRepository.getNowYearTerm()
            success(yearTerm.yearList, yearTerm.termList,
                    yearTerm.yearIndex, yearTerm.termIndex, getTitle(yearTerm))
        }
    }

    fun initScoreList(success: suspend (scores: List<Score>,
                                        ies: Pair<String, String>,
                                        cnt: Pair<String, String>,
                                        gpa: String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            event.showDialog()
            yearTerm = mRepository.getNowYearTerm()
            //获取数据库数据
            scoreList = getLocalScoreList(yearTerm.yearStr, yearTerm.termStr)
            scoreFilter = mRepository.getScoreFilter()
            //若数据库数据不为空，则先使用数据库数据更新View
            if (scoreList.isNotEmpty()) {
                success(scoreList, getIESPair(scoreList, scoreFilter),
                        getScoreCountPair(scoreList), getGPA(scoreList))
            }
            //在前台从教务管理系统获取成绩信息并更新View
            val job = fetchScoreList(true) { scores, ies, cnt, gpa ->
                success(scores, ies, cnt, gpa)
            }
            //数据库数据为空，等待网络数据返回，线程阻塞
            //不为空，则在后台获取网络数据并刷新界面
            if (scoreList.isEmpty()) {
                job.join()
            }
            event.hideDialog()
        }
    }

    fun switchYearAndTerm(yearIndex: Int, termIndex: Int,
                          success: suspend (scores: List<Score>,
                                            ies: Pair<String, String>,
                                            cnt: Pair<String, String>,
                                            gpa: String,
                                            title: String) -> Unit) {
        GlobalScope.launch {
            yearTerm.setYearTermIndex(yearIndex, termIndex)
            scoreList = getLocalScoreList(yearTerm.yearStr, yearTerm.termStr)
            success(scoreList, getIESPair(scoreList, scoreFilter), getScoreCountPair(scoreList),
                    getGPA(scoreList), getTitle(yearTerm))
        }
    }

    fun refreshScoreList(success: suspend (scores: List<Score>,
                                           ies: Pair<String, String>,
                                           cnt: Pair<String, String>,
                                           gpa: String) -> Unit) {
        fetchScoreList(false, success)
    }

    private fun fetchScoreList(isRunOnBackGround: Boolean,
                               success: suspend (scores: List<Score>,
                                                 ies: Pair<String, String>,
                                                 cnt: Pair<String, String>,
                                                 gpa: String) -> Unit): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            isRunOnBackGround.ifFalse {
                event.showDialog()
            }
            try {
                scoreList = ensureLoginStatus {
                    mRepository.fetchScoreList().apply {
                        if (isNotEmpty()) {
                            mRepository.deleteAllScore()
                            mRepository.saveScore(this)
                            val scoreFilter = mRepository.getScoreFilter()
                            scoreFilter.account = mRepository.account
                            scoreFilter.filter(this)
                            mRepository.saveScoreFilter(scoreFilter)
                        }
                    }.filter {
                        it.year == yearTerm.yearStr && it.term == yearTerm.termStr
                    }
                } ?: kotlin.run {
                    event.hideDialog()
                    return@launch
                }
                //筛选不计入智育分的成绩的id并保存
                scoreFilter = mRepository.getScoreFilter()
                scoreFilter.filter(scoreList)
                mRepository.saveScoreFilter(scoreFilter)
                success(scoreList,
                        getIESPair(scoreList, scoreFilter),
                        getScoreCountPair(scoreList),
                        getGPA(scoreList))
                isRunOnBackGround.ifFalse {
                    event.showMessage("刷新成功")
                }
            } catch (e: Exception) {
                if (!isRunOnBackGround) {
                    event.showMessage(e.errorMessage())
                }
                e.printStackTrace()
            }
            isRunOnBackGround.ifFalse {
                event.hideDialog()
            }
        }
    }

    fun updateIES(success: suspend (Pair<String, String>) -> Unit) {
        GlobalScope.launch {
            scoreFilter = mRepository.getScoreFilter()
            success(getIESPair(scoreList, scoreFilter))
        }
    }

    fun iesCalculationDetail(success: suspend (text: String) -> Unit) {
        GlobalScope.launch {
            val totalScore = scoreList
            val calcJGList = ArrayList<Score>()
            val calcNoJGList = ArrayList<Score>()
            val filterList = ArrayList<Score>()
            totalScore.forEach {
                if (it.id !in scoreFilter.filterList) {
                    if (it.realScore >= 60) {
                        calcJGList.add(it)
                    } else {
                        calcNoJGList.add(it)
                    }
                } else {
                    filterList.add(it)
                }
            }
            var first = true
            val sb = StringBuilder("总共${totalScore.size}门成绩，")
            sb.append("排除课程：[")
            filterList.forEach {
                if (first) {
                    first = false
                } else {
                    sb.append("、")
                }
                sb.append(it.name).append(when {
                    it.nature.contains("任意选修") -> "(任意选修课)"
                    it.name.contains("体育") -> "(体育课)"
                    else -> "(自定义)"
                })
            }
            sb.append("]，还有${calcJGList.size + calcNoJGList.size}门纳入计算范围，")
            sb.append("其中${calcNoJGList.size}门课程不及格。加权总分为")
            var totalCalcScore = 0F
            first = true
            (calcJGList + calcNoJGList).forEach {
                if (first) {
                    first = false
                } else {
                    sb.append(" + ")
                }
                sb.append("${it.realScore.trimEnd(2)} × ${it.credit.trimEnd(2)}")
                totalCalcScore += (it.realScore * it.credit)
            }
            sb.append(" = ${totalCalcScore.trimEnd(2)}，总学分为")
            first = true
            var totalCredit = 0F
            (calcJGList + calcNoJGList).forEachIndexed { index, score ->
                if (index == 0) {
                    sb.append(score.credit.trimEnd(2))
                } else {
                    sb.append(" + ${score.credit.trimEnd(2)}")
                }
                totalCredit += score.credit
            }
            var ies = totalCalcScore / totalCredit
            if (ies.isNaN()) {
                ies = 0F
            }
            sb.append(" = ${totalCredit.trimEnd(2)}，则${totalCalcScore.trimEnd(2)} / ${totalCredit.trimEnd(2)} = ${ies.trimEnd(2)}分")
            var totalMinus = 0F
            calcNoJGList.forEach {
                totalMinus += it.credit
            }
            sb.append("，减去不及格的学分共${totalMinus.trimEnd(2)}分，最终为${(ies - totalMinus).trimEnd(2)}分。")
            success(sb.toString())
        }
    }

    fun getYearAndTerm(success: (year: String, term: String) -> Unit) {
        success(yearTerm.yearStr, yearTerm.termStr)
    }

    private fun getLocalScoreList(year: String, term: String): List<Score> {
        return if (year == "全部" && term == "全部") {
            mRepository.getAllScores()
        } else if (year == "全部") {
            mRepository.getScoresByTerm(term)
        } else if (term == "全部") {
            mRepository.getScoresByYear(year)
        } else {
            mRepository.getScores(year, term)
        }
    }

    private fun getTitle(yearTerm: YearTerm): String {
        return if (yearTerm.termStr == "全部") {
            "${yearTerm.yearStr}学年全部学期学习成绩"
        } else {
            "${yearTerm.yearStr}学年第${yearTerm.termStr}学期学习成绩"
        }
    }

    private fun getGPA(scores: List<Score>): String {
        var totalGPA = 0F
        scores.forEach {
            totalGPA += it.gpa
        }
        return if (totalGPA < 0) {
            "无信息"
        } else {
            totalGPA.trimEnd(2)
        }
    }

    private fun getScoreCountPair(scores: List<Score>): Pair<String, String> {
        return Pair(scores.size.toString(), "门")
    }

    private fun getIESPair(scores: List<Score>, scoreFilter: ScoreFilter): Pair<String, String> {
        val ies = scoreFilter.calcIES(scores)
        return if (ies.isNaN() || ies <= 0F) {
            Pair("0", "分")
        } else {
            val result = ies.trimEnd(2)
            val index = result.indexOf('.')
            if (index == -1) {
                Pair(result, "分")
            } else {
                Pair(result.substring(0, index), result.substring(index) + "分")
            }
        }
    }
}