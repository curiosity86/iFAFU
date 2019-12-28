package cn.ifafu.ifafu.mvp.score_filter

import android.app.Activity
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.local.LocalDataSource
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.entity.ScoreFilter
import cn.ifafu.ifafu.util.GlobalLib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreFilterViewModel(private val repository: LocalDataSource) : BaseViewModel() {

    private lateinit var filter: ScoreFilter
    private lateinit var scores: List<Score>

    fun init(activity: Activity, success: suspend (scores: List<Score>, ies: String) -> Unit, fail: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val year: String? = activity.intent.getStringExtra("year")
            val term: String? = activity.intent.getStringExtra("term")
            if (year == null || term == null) {
                fail("未找到相关学期成绩")
                return@launch
            }
            scores = repository.getScores(year, term)
            filter = repository.getScoreFilter()
            scores.forEach {
                it.isIESItem = it.id !in filter.filterList
            }
            success(scores, GlobalLib.formatFloat(GlobalLib.getIES(scores, filter), 2))
        }
    }

    fun itemChecked(score: Score, success: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            if (!score.isIESItem) {
                filter.filterList.add(score.id)
            } else {
                filter.filterList.removeAll { score.id == it }
            }
            repository.saveScoreFilter(filter)
            success(GlobalLib.formatFloat(GlobalLib.getIES(scores, filter), 2))
        }
    }

    fun allChecked(success: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            scores.forEach {
                filter.filterList.add(it.id)
            }
            repository.saveScoreFilter(filter)
            success(GlobalLib.formatFloat(GlobalLib.getIES(scores, filter), 2))
        }
    }
}