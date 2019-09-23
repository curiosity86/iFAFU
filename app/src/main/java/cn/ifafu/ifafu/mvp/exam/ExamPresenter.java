package cn.ifafu.ifafu.mvp.exam;

import java.util.Map;

import cn.ifafu.ifafu.data.entity.YearTerm;
import cn.ifafu.ifafu.base.ifafu.BaseZFPresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

class ExamPresenter extends BaseZFPresenter<ExamContract.View, ExamContract.Model>
        implements ExamContract.Presenter {

    private YearTerm yearTerm;

    private String mCurrentYear;
    private String mCurrentTerm;

    ExamPresenter(ExamContract.View view) {
        super(view, new ExamModel(view.getContext()));
    }

    @Override
    public void onCreate() {
        mCompDisposable.add(mModel.getYearTermList()
                .doOnNext(yearTerm -> this.yearTerm = yearTerm)
                .flatMap(map -> {
                    Map<String, String> map2 = mModel.getYearTerm();
                    mCurrentYear = map2.get("xnd");
                    mCurrentTerm = map2.get("xqd");
                    return mModel.getExamsFromDB(mCurrentYear, mCurrentTerm);
                })
                .flatMap(exams -> {
                    if (exams.isEmpty()) {
                        return mModel.getExamsFromNet(mCurrentYear, mCurrentTerm);
                    } else {
                        return Observable.just(exams);
                    }
                })
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(d -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setYearTermData(yearTerm.getYearList(), yearTerm.getTermList());
                    mView.setYearTermOptions(
                            yearTerm.yearIndexOf(mCurrentYear),
                            yearTerm.termIndexOf(mCurrentTerm));
                    mView.setExamAdapterData(list);
                }, throwable -> {
                    mView.setYearTermData(yearTerm.getYearList(), yearTerm.getTermList());
                    mView.setYearTermOptions(
                            yearTerm.yearIndexOf(mCurrentYear),
                            yearTerm.termIndexOf(mCurrentTerm));
                    onError(throwable);
                })
        );
    }

    @Override
    public void update() {
        mCompDisposable.add(mModel.getExamsFromNet(mCurrentYear, mCurrentTerm)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> mView.setExamAdapterData(list), this::onError)
        );
    }

    @Override
    public void switchYearTerm(int op1, int op2) {
        mCurrentYear = yearTerm.getYear(op1);
        mCurrentTerm = yearTerm.getTerm(op2);
        mCompDisposable.add(mModel
                .getExamsFromDB(mCurrentYear, mCurrentTerm)
                .flatMap(exams -> {
                    if (exams.isEmpty()) {
                        return mModel.getExamsFromNet(mCurrentYear, mCurrentTerm);
                    } else {
                        return Observable.just(exams);
                    }
                })
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(d -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setExamAdapterData(list);
                }, this::onError)
        );
    }

    @Override
    protected void onError(Throwable throwable) {
        mView.showEmptyView();
        super.onError(throwable);
    }
}
