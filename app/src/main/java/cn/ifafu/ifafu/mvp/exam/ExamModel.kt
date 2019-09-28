package cn.ifafu.ifafu.mvp.exam

import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.ifafu.BaseZFModel
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.YearTerm
import cn.ifafu.ifafu.data.entity.ZhengFang
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.ExamParser
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class ExamModel(context: Context) : BaseZFModel(context), ExamContract.Model {

    private var yearTerm: Pair<String, String> = getYearTerm()

    override fun getExamsFromNet(year: String, term: String): Observable<MutableList<Exam>> {
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
                .compose(sort())
    }

    override fun getExamsFromDB(year: String, term: String): Observable<MutableList<Exam>> {
        return Observable.fromCallable { repository.getExams(year, term) }
                .compose(sort())
    }

    override fun getYearTermList(): Observable<YearTerm> {
        return Observable.fromCallable { repository.yearTermList }
    }

    override fun getYearTerm(): Pair<String, String> {
        return repository.yearTerm
    }

    override fun save(list: List<Exam>) {
        repository.saveExam(list)
    }

    private fun sort(): ObservableTransformer<MutableList<Exam>, MutableList<Exam>> {
        val now = System.currentTimeMillis()
        return ObservableTransformer { t ->
            t.doOnNext {
                it.sortedWith(Comparator { o1, o2 ->
                    if (o1.endTime < now && o2.endTime < now) {
                        o2.endTime.compareTo(o1.endTime)
                    } else if (o1.endTime < now || o2.endTime < now) {
                        -1
                    } else {
                        o1.endTime.compareTo(o2.endTime)
                    }
                })
            }
        }
    }

}
