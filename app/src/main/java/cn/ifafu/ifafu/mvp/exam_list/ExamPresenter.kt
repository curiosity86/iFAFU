package cn.ifafu.ifafu.mvp.exam_list

import cn.ifafu.ifafu.base.mvp.BasePresenter
import cn.ifafu.ifafu.base.mvp.addDisposable
import cn.ifafu.ifafu.entity.Exam
import cn.ifafu.ifafu.entity.YearTerm
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.Observable

internal class ExamPresenter(view: ExamContract.View) : BasePresenter<ExamContract.View, ExamContract.Model>(view, ExamModel(view.context)), ExamContract.Presenter {

    private lateinit var yearTerm: YearTerm

    private lateinit var mCurrentYear: String
    private lateinit var mCurrentTerm: String

    override fun onCreate() {
        addDisposable {
            Observable
                    .fromCallable {
                        val yearTerm = mModel.getYearTerm()
                        mCurrentYear = yearTerm.first
                        mCurrentTerm = yearTerm.second
                    }.flatMap {
                        mModel.getYearTermList()
                    }
                    .flatMap {
                        this.yearTerm = it
                        getExams(mCurrentYear, mCurrentTerm, false)
                    }
                    .compose(RxUtils.ioToMain())
                    .compose(showHideLoading())
                    .subscribe({ list ->
                        mView.setYearTermData(yearTerm.yearList, yearTerm.termList)
                        mView.setYearTermOptions(
                                yearTerm.yearIndexOf(mCurrentYear),
                                yearTerm.termIndexOf(mCurrentTerm))
                        mView.setExamAdapterData(list)
                        update()
                    }, this::onError)
        }
    }

    override fun update() {
        mCompDisposable.add(mModel.getExamsFromNet(mCurrentYear, mCurrentTerm)
                .compose(RxUtils.ioToMain())
                .compose(showHideLoading())
                .subscribe({ list -> mView.setExamAdapterData(list) }, this::onError)
        )
    }

    override fun switchYearTerm(op1: String, op2: String) {
        mCompDisposable.add(getExams(op1, op2, false)
                .compose(RxUtils.ioToMain())
                .subscribe({ list -> mView.setExamAdapterData(list) }, this::onError)
        )
    }

    override fun cancelLoading() {
        mCompDisposable.clear()
    }

    private fun getExams(op1: String, op2: String, update: Boolean): Observable<List<Exam>> {
        return if (update) {
            mModel.getExamsFromNet(op1, op2)
        } else {
            mModel.getExamsFromDB(op1, op2)
                    .flatMap { exams ->
                        if (exams.isEmpty()) {
                            mModel.getExamsFromNet(op1, op2)
                        } else {
                            Observable.just(exams)
                        }
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
