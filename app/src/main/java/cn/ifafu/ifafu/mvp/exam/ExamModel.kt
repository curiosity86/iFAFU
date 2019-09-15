package cn.ifafu.ifafu.mvp.exam

import android.annotation.SuppressLint
import android.content.Context
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.ZhengFang
import cn.ifafu.ifafu.data.http.APIManager
import cn.ifafu.ifafu.data.http.parser.ExamParser
import cn.ifafu.ifafu.mvp.base.BaseZFModel
import io.reactivex.Observable
import java.util.*

class ExamModel(context: Context) : BaseZFModel(context), ExamContract.Model {

    private lateinit var toYear: String
    private lateinit var toTerm: String

    fun getThisTermExams(): List<Exam> {
        val map = yearTerm
        return repository.getExams(map["xnd"], map["xqd"])
    }

    override fun getExamsFromNet(year: String, term: String): Observable<MutableList<Exam>> {
        val user = repository.loginUser
        val examUrl = School.getUrl(ZhengFang.EXAM, user)
        val mainUrl = School.getUrl(ZhengFang.MAIN, user)
        return initParams(examUrl, mainUrl)
                .flatMap { params ->
                    //考试查询特例，查询本学期考试只能通过GET
                    if (year == toYear && term == toTerm) {
                        APIManager.getZhengFangAPI()
                                .initParams(examUrl, mainUrl)
                    } else {
                        params["xnd"] = year
                        params["xqd"] = term
                        APIManager.getZhengFangAPI()
                                .getInfo(examUrl, examUrl, params)
                    }
                            .map { it.string() }
                            .compose(ExamParser(user))
                            .map { it.body }
                            .doOnNext { save(it) }
                }
    }

    override fun getExamsFromDB(year: String, term: String): Observable<List<Exam>> {
        return Observable.fromCallable { repository.getExams(year, term) }
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTermList(): Map<String, List<String>> {
        val yearList = ArrayList<String>()
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 6)
        val year = c.get(Calendar.YEAR)
        for (i in 0..3) {
            yearList.add(String.format("%d-%d", year - i - 1, year - i))
        }
        val termList = listOf("1", "2", "3")
        return mapOf("xnd" to yearList, "xqd" to termList)
    }

    @SuppressLint("DefaultLocale")
    override fun getYearTerm(): Map<String, String> {
        val c = Calendar.getInstance()
        c.add(Calendar.MONTH, 6)
        HashMap<String, String>()
        toTerm = if (c.get(Calendar.MONTH) < 8) "1" else "2"
        val year = c.get(Calendar.YEAR)
        toYear = String.format("%d-%d", year - 1, year)
        return mapOf("xnd" to toYear, "xqd" to toTerm)
    }


    override fun save(list: List<Exam>) {
        repository.saveExam(list)
    }

}
