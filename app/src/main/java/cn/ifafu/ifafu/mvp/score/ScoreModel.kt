package cn.ifafu.ifafu.mvp.score

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.entity.ZhengFang
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.ScoreParser
import cn.ifafu.ifafu.mvp.base.BaseZFModel
import cn.ifafu.ifafu.mvp.score.ScoreContract.Model
import io.reactivex.Observable
import java.util.*

class ScoreModel(context: Context?) : BaseZFModel(context), Model {

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
        val user: User = repository.user
        val scoreUrl: String = School.getUrl(ZhengFang.SCORE, user)
        val mainUrl = School.getUrl(ZhengFang.MAIN, user)
        return initParams(scoreUrl, mainUrl)
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
            repository.allScores
        } else if (year == "全部") {
            repository.getScoresByTerm(term)
        } else if (term == "全部") {
            repository.getScoresByYear(year)
        } else {
            repository.getScores(year, term)
        }
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTermList(): Observable<Map<String, List<String>>> {
        return Observable.fromCallable {
            val yearList: MutableList<String> = ArrayList()
            val c: Calendar = Calendar.getInstance()
            c.add(Calendar.MONTH, 6)
            val year = c.get(Calendar.YEAR)
            for (i in 0..3) {
                yearList.add(String.format("%d-%d", year - i - 1, year - i))
            }
            yearList.add("全部")
            val termList: List<String> = listOf("1", "2", "全部")
            val map: MutableMap<String, List<String>> = HashMap()
            map["ddlxn"] = yearList
            map["ddlxq"] = termList
            map
        }
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTerm(): Observable<Map<String, String>> {
        return Observable.fromCallable {
            val c: Calendar = Calendar.getInstance()
            c.add(Calendar.MONTH, 6)
            val map: MutableMap<String, String> = HashMap()
            map["ddlxq"] = if (c.get(Calendar.MONTH) < 8) "1" else "2"
            val year = c.get(Calendar.YEAR)
            map["ddlxn"] = String.format("%d-%d", year - 1, year)
            map
        }
    }

}