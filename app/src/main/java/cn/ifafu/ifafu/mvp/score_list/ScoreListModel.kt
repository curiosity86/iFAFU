package cn.ifafu.ifafu.mvp.score_list

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.ScoreParser
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.entity.YearTerm
import cn.ifafu.ifafu.entity.ZhengFang
import cn.ifafu.ifafu.mvp.score_list.ScoreListContract.Model
import io.reactivex.Observable

class ScoreListModel(context: Context) : BaseZFModel(context), Model {

    override fun getScoresFromNet(year: String, term: String): Observable<List<Score>> {
        return getScoresFromNet()
                .map { list ->
                    list.filter {
                        year == "全部" && term == "全部"
                                || year == "全部" && it.term == term
                                || term == "全部" && it.year == year
                                || it.year == year && it.term == term
                    }
                }
    }

    override fun getScoresFromNet(): Observable<MutableList<Score>> {
        val user by lazy { repository.getInUseUser()!! }
        var scoreUrl = ""
        var mainUrl = ""
        return Observable.fromCallable {  }
                .flatMap {
                    scoreUrl = School.getUrl(ZhengFang.SCORE, user)
                    mainUrl = School.getUrl(ZhengFang.MAIN, user)
                    initParams(scoreUrl, mainUrl)
                }
                .flatMap { params: MutableMap<String, String> ->
                    when (user.schoolCode) {
                        School.FAFU -> {
                            params["ddlxn"] = "全部"
                            params["ddlxq"] = "全部"
                            params["btnCx"] = " ��  ѯ "
                        }
                        School.FAFU_JS -> {
                            params["ddlXN"] = ""
                            params["ddlXQ"] = ""
                            params["ddl_kcxz"] = ""
                            params["btn_zcj"] = "����ɼ�"
                        }
                    }
                    APIManager.getZhengFangAPI()
                            .getInfo(scoreUrl, scoreUrl, params)
                            .compose(ScoreParser(user))
                            .doOnNext {
                                if (it.isNotEmpty()) {
                                    repository.deleteAllScore()
                                    repository.saveScore(it)
                                }
                            }
                }
    }

    override fun getScoresFromDB(year: String, term: String): List<Score> {
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

    @SuppressLint("DefaultLocale")
    override fun getYearTermList(): Observable<YearTerm> {
        return Observable.fromCallable {
            repository.getYearTermList().apply {
                addTerm("全部")
                addYear("全部")
            }
        }
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTerm(): Observable<Pair<String, String>> {
        return Observable.fromCallable {
            repository.getYearTerm()
        }
    }

}