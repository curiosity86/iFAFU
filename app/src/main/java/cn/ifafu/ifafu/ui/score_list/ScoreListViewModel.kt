package cn.ifafu.ifafu.ui.score_list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.ScoreFilter
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class ScoreListViewModel(application: Application) : BaseViewModel(application) {

    val semester = MutableLiveData<Semester>()
    val scoreList = MutableLiveData<List<Score>>()
    val ies = MutableLiveData<Pair<String, String>>()
    val cnt = MutableLiveData<Pair<String, String>>()
    val gpa = MutableLiveData<String>()
    val iesDetail = MutableLiveData<String>()

    private lateinit var scoreFilter: ScoreFilter

    fun initData() {
        safeLaunchWithMessage {
            event.showDialog()
            val semester = RepositoryImpl.getNowSemester()
            this@ScoreListViewModel.semester.postValue(semester)
            //postValue有延迟
            withContext(Dispatchers.Main) {
                this@ScoreListViewModel.semester.value = semester
            }
            //获取数据库数据
            val scores = RepositoryImpl.ScoreRt.getNow()
            scoreFilter = RepositoryImpl.ScoreRt.getFilter()
            //若数据库数据不为空，则先使用数据库数据更新View
            if (scores.isNotEmpty()) {
                this@ScoreListViewModel.scoreList.postValue(scores)
                this@ScoreListViewModel.ies.postValue(getIESPair(scores, scoreFilter))
                this@ScoreListViewModel.cnt.postValue(getScoreCountPair(scores))
                this@ScoreListViewModel.gpa.postValue(getGPA(scores))
            }
            //在前台从教务管理系统获取成绩信息并更新View
            val job = fetchScoreList(true)
            //数据库数据为空，等待网络数据返回，线程阻塞
            //不为空，则在后台获取网络数据并刷新界面
            if (scores.isEmpty()) {
                job.join()
            }
            event.hideDialog()
        }
    }

    fun switchYearAndTerm(yearIndex: Int, termIndex: Int) {
        safeLaunchWithMessage {
            val semester = semester.value!!
            semester.setYearTermIndex(yearIndex, termIndex)
            val scores = RepositoryImpl.ScoreRt.getAll(semester.yearStr, semester.termStr)
            this@ScoreListViewModel.semester.postValue(semester)
            this@ScoreListViewModel.scoreList.postValue(scores)
            this@ScoreListViewModel.ies.postValue(getIESPair(scores, scoreFilter))
            this@ScoreListViewModel.cnt.postValue(getScoreCountPair(scores))
            this@ScoreListViewModel.gpa.postValue(getGPA(scores))
        }
    }

    fun refreshScoreList() {
        fetchScoreList(false)
    }

    private fun fetchScoreList(isRunOnBackGround: Boolean): Job {
        return safeLaunchWithMessage {
            if (!isRunOnBackGround) {
                event.showDialog()
            }
            val semester = this@ScoreListViewModel.semester.value!!
            val scores = ensureLoginStatus {
                RepositoryImpl.ScoreRt.fetchAll(semester.yearStr, semester.termStr).data
            } ?: kotlin.run {
                event.hideDialog()
                event.showMessage("获取成绩出错")
                return@safeLaunchWithMessage
            }
            this@ScoreListViewModel.scoreList.postValue(scores)
            this@ScoreListViewModel.ies.postValue(getIESPair(scores, scoreFilter))
            this@ScoreListViewModel.cnt.postValue(getScoreCountPair(scores))
            this@ScoreListViewModel.gpa.postValue(getGPA(scores))
            if (!isRunOnBackGround) {
                event.showMessage("刷新成功")
                event.hideDialog()
            }
        }
    }

    fun updateIES() {
        safeLaunchWithMessage {
            scoreFilter = RepositoryImpl.ScoreRt.getFilter()
            this@ScoreListViewModel.ies.postValue(getIESPair(scoreList.value!!, scoreFilter))
        }
    }

    fun iesCalculationDetail() {
        safeLaunchWithMessage {
            val all = scoreList.value!!
            val passingList = ArrayList<Score>()
            val failList = ArrayList<Score>()
            val filterList = ArrayList<Score>()
            var totalScore = 0F
            var credit = 0F
            all.forEach {
                if (it.id !in scoreFilter.filterList) {
                    if (it.realScore >= 60) {
                        passingList.add(it)
                    } else {
                        failList.add(it)
                    }
                } else {
                    filterList.add(it)
                }
            }
            iesDetail.postValue("总共${all.size}门成绩，排除课程：[" +
                    StringBuilder().apply {
                        var first = true
                        filterList.forEach {
                            if (first) {
                                first = false
                            } else {
                                append("、")
                            }
                            append(it.name).append(when {
                                it.nature.contains("任意选修") -> "(任意选修课)"
                                it.name.contains("体育") -> "(体育课)"
                                else -> "(自定义)"
                            })
                        }
                    } + "]，还有${passingList.size + failList.size}门纳入计算范围，" +
                    "其中${failList.size}门课程不及格。加权总分为" +
                    StringBuilder().apply {
                        var first = true
                        (passingList + failList).forEach {
                            if (first) {
                                first = false
                            } else {
                                append(" + ")
                            }
                            append("${it.realScore.trimEnd(2)} × ${it.credit.trimEnd(2)}")
                            totalScore += (it.realScore * it.credit)
                        }
                        append(" = ${totalScore.trimEnd(2)}")
                    } + "，总学分为" +
                    StringBuilder().apply {
                        (passingList + failList).forEachIndexed { index, score ->
                            if (index == 0) {
                                append(score.credit.trimEnd(2))
                            } else {
                                append(" + ${score.credit.trimEnd(2)}")
                            }
                            credit += score.credit
                        }
                        append(" = ${credit.trimEnd(2)}")
                    } +
                    StringBuilder().apply {
                        var ies = totalScore / credit
                        var totalMinus = 0F
                        if (ies.isNaN()) {
                            ies = 0F
                        }
                        append("，则${totalScore.trimEnd(2)} / ${credit.trimEnd(2)} = ${ies.trimEnd(2)}分")
                        failList.forEach {
                            totalMinus += it.credit
                        }
                        append("，减去不及格的学分共${totalMinus.trimEnd(2)}分，最终为${(ies - totalMinus).trimEnd(2)}分。")
                    }
            )
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