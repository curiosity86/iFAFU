package cn.ifafu.ifafu.mvp.score_list

import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.entity.ScoreFilter
import cn.ifafu.ifafu.entity.YearTerm
import cn.ifafu.ifafu.util.GlobalLib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreListViewModel(private val repository: Repository) : BaseViewModel() {

    private lateinit var yearTerm: YearTerm

    private var scoreList: List<Score> = ArrayList()

    private var scoreFilter: ScoreFilter = ScoreFilter()

    fun initOptionPickerData(success: suspend (yearList: List<String>,
                                               termList: List<String>,
                                               yearIndex: Int,
                                               termIndex: Int) -> Unit) {
        GlobalScope.launch {
            yearTerm = repository.getNowYearTerm()
            success(yearTerm.yearList, yearTerm.termList,
                    yearTerm.yearIndex, yearTerm.termIndex)
        }
    }

    fun initScoreList(success: suspend (scores: List<Score>,
                                        ies: Pair<String, String>,
                                        cnt: Pair<String, String>,
                                        gpa: String, title: String) -> Unit,
                      fail: suspend (String) -> Unit,
                      before: suspend () -> Unit,
                      final: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            before()
            yearTerm = repository.getNowYearTerm()
            //获取数据库数据
            scoreList = getLocalScoreList(yearTerm.yearStr, yearTerm.termStr)
            scoreFilter = repository.getScoreFilter()
            //若数据库数据不为空，则先使用数据库数据更新View
            if (scoreList.isNotEmpty()) {
                success(scoreList, getIESPair(scoreList, scoreFilter), getScoreCountPair(scoreList),
                        getGPA(scoreList), getTitle(yearTerm))
                //在后台从教务管理系统获取成绩信息并更新View
                refreshScoreList({ scores, ies, cnt, gpa ->
                    success(scores, ies, cnt, gpa, getTitle(yearTerm))
                }, {}, {}, {})
                final()
            } else {
                //在前台从教务管理系统获取成绩信息并更新View
                refreshScoreList({ scores, ies, cnt, gpa ->
                    success(scores, ies, cnt, gpa, getTitle(yearTerm))
                }, fail = fail, before = {}, final = final)
            }
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
                                           gpa: String) -> Unit,
                         fail: suspend (String) -> Unit,
                         before: suspend () -> Unit,
                         final: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            before()
            try {
                scoreList = repository.fetchScoreList(yearTerm.yearStr, yearTerm.termStr)
                repository.saveScore(scoreList)
                //筛选不计入智育分的成绩的id并保存
                scoreFilter = repository.getScoreFilter()
                scoreFilter.filter(scoreList)
                repository.saveScoreFilter(scoreFilter)
                success(scoreList,
                        getIESPair(scoreList, scoreFilter),
                        getScoreCountPair(scoreList),
                        getGPA(scoreList))
            } catch (e: Exception) {
                fail(e.errorMessage())
                e.printStackTrace()
            }
            final()
        }
    }

    fun updateIES(success: suspend (Pair<String, String>) -> Unit) {
        GlobalScope.launch {
            scoreFilter = repository.getScoreFilter()
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
            sb.append("排除任意选修课、体育课、缓考、免修、补修的课程：[")
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
            sb.append("]之外，还有${calcJGList.size + calcNoJGList.size}门纳入计算范围，")
            sb.append("其中共有${calcNoJGList.size}门课程不及格。加权总分为")
            var totalCalcScore = 0F
            first = true
            (calcJGList + calcNoJGList).forEach {
                if (first) {
                    first = false
                } else {
                    sb.append(" + ")
                }
                sb.append(String.format("%.2f × %.2f", it.realScore, it.credit))
                totalCalcScore += (it.realScore * it.credit)
            }
            sb.append(String.format(" = %.2f，总学分为", totalCalcScore))
            first = true
            var totalCredit = 0F
            (calcJGList + calcNoJGList).forEach {
                if (first) {
                    first = false
                } else {
                    sb.append(" + ")
                }
                sb.append(String.format("%.2f", it.credit))
                totalCredit += it.credit
            }
            var ies = totalCalcScore / totalCredit
            if (ies.isNaN()) {
                ies = 0F
            }
            sb.append(String.format(" = %.2f，则%.2f / %.2f = %.2f分", totalCredit, totalCalcScore, totalCredit, ies))
            var totalMinus = 0F
            calcNoJGList.forEach {
                totalMinus += it.credit
            }
            sb.append(String.format("，减去不及格的学分共%.2f分，最终为%.2f分。", totalMinus, ies))
            success(sb.toString())
        }
    }

    fun getYearAndTerm(success: (year: String, term: String) -> Unit) {
        success(yearTerm.yearStr, yearTerm.termStr)
    }

    private fun getLocalScoreList(year: String, term: String): List<Score> {
        return if (year == "全部" && term == "全部") {
            repository.getAllScores()
        } else if (year == "全部") {
            repository.getScoresByTerm(term)
        } else if (term == "全部") {
            repository.getScoresByYear(year)
        } else {
            repository.getScores(year, term)
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
            GlobalLib.formatFloat(totalGPA, 2)
        }
    }

    private fun getScoreCountPair(scores: List<Score>): Pair<String, String> {
        return Pair(scores.size.toString(), "门")
    }

    private fun getIESPair(scores: List<Score>, scoreFilter: ScoreFilter): Pair<String, String> {
        val ies = GlobalLib.getIES(scores, scoreFilter)
        return if (ies.isNaN() || ies <= 0F) {
            Pair("0", "分")
        } else {
            val result = GlobalLib.formatFloat(ies, 2)
            val index = result.indexOf('.')
            if (index == -1) {
                Pair(result, "分")
            } else {
                Pair(result.substring(0, index), result.substring(index) + "分")
            }
        }
    }
}