package cn.ifafu.ifafu.experiment.ui.score_list

import androidx.lifecycle.*
import cn.ifafu.ifafu.data.bean.Semester
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.experiment.data.repository.ScoreRepository
import cn.ifafu.ifafu.experiment.util.successMap
import cn.ifafu.ifafu.experiment.util.toEventLiveData
import cn.ifafu.ifafu.experiment.vo.PairString
import cn.ifafu.ifafu.util.Event
import cn.ifafu.ifafu.util.sumByFloat
import cn.ifafu.ifafu.util.toRadiusString
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreListViewModel(
        private val scoreRepository: ScoreRepository
) : ViewModel() {

    val semester: LiveData<Event<Semester>> = scoreRepository.semester.toEventLiveData()
    val scoresResource: LiveData<Resource<List<Score>>> = scoreRepository.scoreResource
    val ies: LiveData<PairString> = scoreRepository.ies.map {
        if (it.isNaN() || it <= 0F) {
            PairString("0", "分")
        } else {
            val result = it.trimEnd(2)
            val index = result.indexOf('.')
            if (index == -1) {
                PairString(result, "分")
            } else {
                PairString(result.substring(0, index), result.substring(index) + "分")
            }
        }
    }
    val cnt: LiveData<PairString> = scoresResource.successMap { PairString(it.size.toString(), "门") }
    val gpa: LiveData<String> = scoresResource.successMap { scores ->
        scores.filter { it.gpa > 0 }
                .sumByFloat { it.gpa }
                .toRadiusString(2)
    }

    val iesDetail = MutableLiveData<Event<String>>()

    fun switchYearAndTerm(year: String, term: String) {
        scoreRepository.switchSemester(year, term)
    }

    fun refreshScoreList() {
        scoreRepository.refresh()
    }

    fun iesCalculationDetail() = viewModelScope.launch(Dispatchers.IO) {
        val all = (scoresResource.value as? Resource.Success)?.data ?: return@launch
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
        iesDetail.postValue(Event("总共${all.size}门成绩，排除课程：[" +
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
        ))
    }

}