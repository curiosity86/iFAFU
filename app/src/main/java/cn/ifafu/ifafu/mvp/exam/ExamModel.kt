package cn.ifafu.ifafu.mvp.exam

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.YearTerm
import cn.ifafu.ifafu.data.entity.ZhengFang
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.ExamParser
import io.reactivex.Observable

class ExamModel(context: Context) : BaseZFModel(context), ExamContract.Model {

    private var yearTerm: Pair<String, String> = getYearTerm()

    fun getThisTermExams(): List<Exam> {
        return repository.getExams(yearTerm.first, yearTerm.second)
    }

    override fun getExamsFromNet(year: String, term: String): Observable<List<Exam>> {
        val user = repository.loginUser
        val examUrl = School.getUrl(ZhengFang.EXAM, user)
        val mainUrl = School.getUrl(ZhengFang.MAIN, user)
        return initParams(examUrl, mainUrl)
                .flatMap { params ->
                    //考试查询特例，查询本学期考试只能通过GET
                    val observable = if (year == yearTerm.first && term == yearTerm.second) {
                        APIManager.getZhengFangAPI()
                                .initParams(examUrl, mainUrl)
                    } else {
                        params["xnd"] = year
                        params["xqd"] = term
                        APIManager.getZhengFangAPI()
                                .getInfo(examUrl, examUrl, params)
                    }
                    observable.compose(ExamParser(user))
                            .map { it.body }
                            .doOnNext { save(it) }
                }
    }

    override fun getExamsFromDB(year: String, term: String): Observable<List<Exam>> {
        return Observable.fromCallable { repository.getExams(year, term) }
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTermList(): Observable<YearTerm>? {
        return Observable.fromCallable { repository.yearTermList }
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTerm(): Pair<String, String> {
        return repository.yearTerm
    }

    override fun save(list: List<Exam>) {
        repository.saveExam(list)
    }

}
