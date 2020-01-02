package cn.ifafu.ifafu.mvp.score_filter

import android.app.Activity
import android.app.Application
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.entity.ScoreFilter
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreFilterViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var filter: ScoreFilter
    private lateinit var scores: List<Score>

    fun init(activity: Activity, success: suspend (scores: List<Score>, ies: String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val year: String? = activity.intent.getStringExtra("year")
            val term: String? = activity.intent.getStringExtra("term")
            if (year == null || term == null) {
                event.showMessage("未找到相关学期成绩")
                return@launch
            }
            scores = mRepository.getScores(year, term)
            filter = mRepository.getScoreFilter()
            scores.forEach {
                it.isIESItem = it.id !in filter.filterList
            }
            success(scores, filter.calcIES(scores).trimEnd(2))
        }
    }

    fun itemChecked(score: Score, success: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            if (!score.isIESItem) {
                filter.filterList.add(score.id)
            } else {
                filter.filterList.removeAll { score.id == it }
            }
            mRepository.saveScoreFilter(filter)
            success(filter.calcIES(scores).trimEnd(2))
        }
    }

    fun allChecked(success: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            scores.forEach {
                filter.filterList.remove(it.id)
            }
            mRepository.saveScoreFilter(filter)
            success(filter.calcIES(scores).trimEnd(2))
        }
    }
}