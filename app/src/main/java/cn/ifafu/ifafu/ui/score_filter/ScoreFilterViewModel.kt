package cn.ifafu.ifafu.ui.score_filter

import android.app.Activity
import android.app.Application
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.ScoreFilter
import cn.ifafu.ifafu.util.trimEnd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreFilterViewModel(application: Application) : BaseViewModel(application) {

    private lateinit var filter: ScoreFilter
    private lateinit var scores: List<Score>

    fun init(activity: Activity, success: suspend (scores: List<Score>, ies: String) -> Unit) {
        safeLaunchWithMessage {
            val year: String? = activity.intent.getStringExtra("year")
            val term: String? = activity.intent.getStringExtra("term")
            if (year == null || term == null) {
                event.showMessage("未找到相关学期成绩")
                return@safeLaunchWithMessage
            }
            scores = Repository.ScoreRt.getAll(year, term)
            filter = Repository.ScoreRt.getFilter()
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
            Repository.ScoreRt.saveFilter(filter)
            success(filter.calcIES(scores).trimEnd(2))
        }
    }

    fun allChecked(success: suspend (String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            scores.forEach {
                filter.filterList.remove(it.id)
            }
            Repository.ScoreRt.saveFilter(filter)
            success(filter.calcIES(scores).trimEnd(2))
        }
    }
}