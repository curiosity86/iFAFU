package cn.ifafu.ifafu.mvp.exam

import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.base.i.IView
import cn.ifafu.ifafu.base.ifafu.IZFModel
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.YearTerm
import io.reactivex.Observable

class ExamContract {

    interface Presenter : IPresenter {

        fun update()

        fun switchYearTerm(op1: String, op2: String)

        fun cancelLoading()
    }

    interface View : IView {

        fun showEmptyView()

        fun setYearTermOptions(option1: Int, option2: Int)

        fun setExamAdapterData(data: List<Exam>)

        fun setYearTermData(years: List<String>, terms: List<String>)
    }

    interface Model : IZFModel {

        fun getYearTermList(): Observable<YearTerm>

        fun getYearTerm(): Pair<String, String>

        fun getExamsFromNet(year: String, term: String): Observable<MutableList<Exam>>

        fun getExamsFromDB(year: String, term: String): Observable<MutableList<Exam>>

        fun save(list: List<Exam>)

    }
}
