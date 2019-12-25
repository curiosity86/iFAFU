package cn.ifafu.ifafu.mvp.score_filter

import android.content.Context
import cn.ifafu.ifafu.base.BaseModel
import cn.ifafu.ifafu.data.RepositoryImpl
import cn.ifafu.ifafu.entity.Score
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScoreFilterModel constructor(context: Context) : BaseModel(context), ScoreFilterConstant.Model {
    override fun getScoresFromDB(year: String, term: String): Observable<List<Score>> {
        return Observable.fromCallable {
            if (year == "全部" && term == "全部") {
                return@fromCallable repository.getAllScores()
            } else if (year == "全部") {
                return@fromCallable repository.getScoresByTerm(term)
            } else if (term == "全部") {
                return@fromCallable repository.getScoresByYear(year)
            } else {
                return@fromCallable repository.getScores(year, term)
            }
        }
    }

    override fun save(score: Score) {
        GlobalScope.launch(Dispatchers.IO) {
            RepositoryImpl.saveScore(score)
        }
    }
}