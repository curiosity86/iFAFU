package cn.ifafu.ifafu.experiment.score.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.ScoreFilter
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScoreListViewModel(
        application: Application,
        private val coroutineScope: CoroutineScope = GlobalScope
) : BaseViewModel(application) {

    private var scoreFilter = coroutineScope.async {
        RepositoryImpl.ScoreRt.getFilter()
    }

    val scores: MutableLiveData<List<Score>> = MutableLiveData()
    val ies: MutableLiveData<Pair<String, String>> = MutableLiveData()
    val cnt: LiveData<Pair<String, String>> = scores.switchMap { scores ->
        liveData {
            emit(getScoreCountPair(scores))
        }
    }
    val gpa: LiveData<String> = scores.switchMap { scores ->
        liveData {
            emit(getGPA(scores))
        }
    }

    val iesDetail = MutableLiveData<String>()
    val loading = MutableLiveData<String>()

    val semester: MutableLiveData<Semester> = MutableLiveData()
    val title: LiveData<String> = semester.switchMap {
        liveData {
            emit(it.toString())
        }
    }

    init {
        safeLaunchWithMessage {
            loading.postValue("获取中")
            //获取数据库学期信息
            val semester = RepositoryImpl.getNowSemester()
            this@ScoreListViewModel.semester.postValue(semester)
            //先使用数据库数据更新View
            val scores = RepositoryImpl.ScoreRt.getNow()
            val local = launch {
                this@ScoreListViewModel.scores.postValue(scores)
                //更新智育分
                val filter = RepositoryImpl.ScoreRt.getFilter()
                ies.postValue(getIESPair(scores, filter))
            }
            //在前台从教务管理系统获取成绩信息并更新View
            val net = launch {
                val scoreList = fetchAsync(semester).await()
                if (scoreList == null) {
                    toast("获取成绩失败")
                } else {
                    this@ScoreListViewModel.scores.postValue(scoreList)
                    //更新智育分
                    val filter = RepositoryImpl.ScoreRt.getFilter()
                    ies.postValue(getIESPair(scoreList, filter))
                }
            }
            //若数据库不为空则先取消加载dialog
            //为空则等待执行
            local.join()
            if (scores.isEmpty()) {
                net.join()
            }
            loading.postValue(null)
        }
    }

    fun switchYearAndTerm(yearIndex: Int, termIndex: Int) {
        safeLaunchWithMessage {
            val semester = semester.value ?: return@safeLaunchWithMessage
            semester.setYearTermIndex(yearIndex, termIndex)
            val scores = RepositoryImpl.ScoreRt.getAll(semester.yearStr, semester.termStr)
            this@ScoreListViewModel.semester.postValue(semester)
            this@ScoreListViewModel.scores.postValue(scores)
            // 更新智育分
            val filter = RepositoryImpl.ScoreRt.getFilter()
            ies.postValue(getIESPair(scores, filter))
        }
    }

    fun refreshScoreList() = coroutineScope.launch {
        loading.postValue("获取中")
        val semester = semester.value ?: let {
            toast("无法获取学期信息")
            loading.postValue(null)
            return@launch
        }
        val scores = fetchAsync(semester).await()
        if (scores == null) {
            toast("成绩获取失败")
        } else {
            this@ScoreListViewModel.scores.postValue(scores)
            val filter = RepositoryImpl.ScoreRt.getFilter()
            ies.postValue(getIESPair(scores, filter))
            toast("成绩获取成功")
        }
        loading.postValue(null)
    }

    fun updateIES() = coroutineScope.launch {
        val scores = scores.value ?: return@launch
        val filter = RepositoryImpl.ScoreRt.getFilter()
        ies.postValue(getIESPair(scores, filter))
    }

    fun iesCalculationDetail() {
        safeLaunchWithMessage {
            val all = scores.value!!
            val passingList = ArrayList<Score>()
            val failList = ArrayList<Score>()
            val filterList = ArrayList<Score>()
            var totalScore = 0F
            var credit = 0F
            all.forEach {
                if (it.id !in scoreFilter.await().filterList) {
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
            iesDetail.postValue(null)
        }
    }

    private fun fetchAsync(semester: Semester) = coroutineScope.async {
        try {
            return@async ensureLoginStatus {
                RepositoryImpl.ScoreRt.fetchAll(semester.yearStr, semester.termStr).data
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@async null
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