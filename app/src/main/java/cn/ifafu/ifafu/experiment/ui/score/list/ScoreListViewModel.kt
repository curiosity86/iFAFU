package cn.ifafu.ifafu.experiment.ui.score.list

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.calcIES
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.experiment.vo.PairString
import cn.ifafu.ifafu.util.Event
import cn.ifafu.ifafu.util.sumByFloat
import cn.ifafu.ifafu.util.toString
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScoreListViewModel(
        application: Application,
        private val coroutineScope: CoroutineScope = GlobalScope
) : BaseViewModel(application) {

    /**
     * _score是唯一一个需要修改的私有LiveData，其余都通过监听_score值的改变而变化
     * LiveDate配合Room, JetPack牛逼！！
     */
    private val _scores = MutableLiveData<List<Score>>()
    private val _ies = MediatorLiveData<PairString>().apply {
        //监听成绩列表的修改
        addSource(_scores) { scores ->
            this.value = getIESPair(scores)
        }
    }
    private val _cnt = _scores.map { scores ->
        PairString(scores.size.toString(), "门")
    }
    private val _gpa = _scores.map { scores ->
        scores.filter { it.gpa > 0 }
                .sumByFloat { it.gpa }
                .toString(2)
    }

    //TODO 从Repository获取LiveData<Score>, 是否从网络获取在Repository内判断
    val scores: LiveData<List<Score>> = _scores
    val gpa: LiveData<String> = _gpa
    val ies: LiveData<PairString> = _ies
    val cnt: LiveData<PairString> = _cnt

    val iesDetail = MutableLiveData<String>()
    val loading = MutableLiveData<String>()
    val semester = MutableLiveData<Semester>()
    val message = MutableLiveData<Event<String>>()

    init {
        loading.value = "获取中"
        safeLaunchWithMessage {
            //获取数据库学期信息
            val semester = RepositoryImpl.getNowSemester()
            this@ScoreListViewModel.semester.postValue(semester)
            //先使用数据库数据更新View
            val scores = RepositoryImpl.ScoreRt.getNow()
            val local = launch {
                _scores.postValue(scores)
            }
            //在前台从教务管理系统获取成绩信息并更新View
            val net = launch {
                val scoreList = fetchAsync(semester).await()
                if (scoreList == null) {
                    message.postValue(Event("获取成绩失败"))
                } else {
                    _scores.postValue(scoreList)
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
            _scores.postValue(scores)
        }
    }

    fun refreshScoreList() = coroutineScope.launch {
        loading.postValue("获取中")
        val semester = semester.value ?: let {
            message.postValue(Event("无法获取学期信息"))
            loading.postValue(null)
            return@launch
        }
        val scores = fetchAsync(semester).await()
        if (scores == null) {
            message.postValue(Event("成绩获取失败"))
        } else {
            _scores.postValue(scores)
            message.postValue(Event("成绩获取成功"))
        }
        loading.postValue(null)
    }

    fun iesCalculationDetail() {
        safeLaunchWithMessage {
            val all = scores.value!!
            val passingList = ArrayList<Score>() //及格
            val failList = ArrayList<Score>() //不及格
            val filterList = ArrayList<Score>() //过滤
            var totalScore = 0F
            var credit = 0F
            all.forEach {
                if (!it.isIESItem) {
                    filterList.add(it)
                } else if (it.realScore >= 60) {
                    passingList.add(it)
                } else {
                    failList.add(it)
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

    private fun getIESPair(scores: List<Score>): PairString {
        val ies = scores.calcIES()
        return if (ies.isNaN() || ies <= 0F) {
            PairString("0", "分")
        } else {
            val result = ies.trimEnd(2)
            val index = result.indexOf('.')
            if (index == -1) {
                PairString(result, "分")
            } else {
                PairString(result.substring(0, index), result.substring(index) + "分")
            }
        }
    }
}