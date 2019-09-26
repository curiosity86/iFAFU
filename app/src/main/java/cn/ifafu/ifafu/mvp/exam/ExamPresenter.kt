package cn.ifafu.ifafu.mvp.exam

import cn.ifafu.ifafu.base.ifafu.BaseZFPresenter
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.YearTerm
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

internal class ExamPresenter(view: ExamContract.View) : BaseZFPresenter<ExamContract.View, ExamContract.Model>(view, ExamModel(view.context)), ExamContract.Presenter {

    private lateinit var yearTerm: YearTerm

    private lateinit var mCurrentYear: String
    private lateinit var mCurrentTerm: String

    override fun onCreate() {
        mCompDisposable.add(mModel.yearTermList
                .flatMap {
                    this.yearTerm = it
                    val yearTerm = mModel.yearTerm
                    mCurrentYear = yearTerm.first
                    mCurrentTerm = yearTerm.second
                    getExams(mCurrentYear, mCurrentTerm)
                }
                .subscribe({ list ->
                    mView.setYearTermData(yearTerm.yearList, yearTerm.termList)
                    mView.setYearTermOptions(
                            yearTerm.yearIndexOf(mCurrentYear),
                            yearTerm.termIndexOf(mCurrentTerm))
                    mView.setExamAdapterData(list)
                }, this::onError)
        )
    }

    override fun update() {
        mCompDisposable.add(mModel.getExamsFromNet(mCurrentYear, mCurrentTerm)
                .compose(RxUtils.ioToMain())
                .compose(showHideLoading())
                .subscribe({ list -> mView.setExamAdapterData(list) }, this::onError)
        )
    }

    override fun switchYearTerm(op1: String, op2: String) {
        mCompDisposable.add(getExams(op1, op2)
                .subscribe({ list -> mView.setExamAdapterData(list) }, this::onError)
        )
    }

    private fun getExams(op1: String, op2: String): Observable<MutableList<Exam>> {
        return mModel
                .getExamsFromDB(op1, op2)
                .flatMap { exams ->
                    if (exams.isEmpty()) {
                        mModel.getExamsFromNet(mCurrentYear, mCurrentTerm)
                    } else {
                        Observable.just(exams)
                    }
                }
                .compose(sort())
                .compose(RxUtils.ioToMain())
                .compose(showHideLoading())
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

    override fun onError(throwable: Throwable) {
        mView.setYearTermData(yearTerm.yearList, yearTerm.termList)
        mView.setYearTermOptions(
                yearTerm.yearIndexOf(mCurrentYear),
                yearTerm.termIndexOf(mCurrentTerm))
        mView.showEmptyView()
        super.onError(throwable)
    }
}
