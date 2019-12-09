package cn.ifafu.ifafu.mvp.exam_list

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

    override fun getExamsFromNet(year: String, term: String): Observable<List<Exam>> {
        val user = repository.loginUser
        val examUrl = School.getUrl(ZhengFang.EXAM, user)
        val mainUrl = School.getUrl(ZhengFang.MAIN, user)
        return initParams(examUrl, mainUrl)
                .flatMap { params ->
                    //考试查询特例，查询本学期考试只能通过GET
                    if (year == yearTerm.first && term == yearTerm.second) {
                        APIManager.getZhengFangAPI()
                                .initParams(examUrl, mainUrl)
                                .compose(ExamParser(user))
                                .map { it.body }
                                .doOnNext { save(it) }
                    } else {
                        params["xnd"] = year
                        params["xqd"] = term
                        APIManager.getZhengFangAPI()
                                .getInfo(examUrl, examUrl, params)
                                .compose(ExamParser(user))
                                .map { it.body }
                                .doOnNext { save(it) }
                    }
                }
                .map { sort(it) }
    }

    override fun getExamsFromDB(year: String, term: String): Observable<List<Exam>> {
        return Observable.fromCallable { repository.getExams(year, term) }
                .map { sort(it) }
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

    private fun sort(it: MutableList<Exam>): MutableList<Exam> {
        val now = System.currentTimeMillis()
        return it.sortedWith(Comparator { o1, o2 ->
            if (o1.startTime == 0L && o2.startTime == 0L) {
                o1.id.compareTo(o2.id)
            } else if (o1.startTime == 0L) {
                1
            } else if (o2.startTime == 0L) {
                -1
            } else if (o1.endTime > now && o2.endTime > now) {
                o1.endTime.compareTo(o2.endTime)
            } else if (o1.endTime < now) {
                1
            } else if (o2.endTime < now) {
                -1
            } else {
                o1.endTime.compareTo(o2.endTime)
            }
        }).toMutableList()
    }
}
