package cn.ifafu.ifafu.mvp.exam;

import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

class ExamPresenter extends BaseZFPresenter<ExamContract.View, ExamContract.Model>
        implements ExamContract.Presenter {

    private List<String> years;
    private List<String> terms;

    private String mCurrentYear;
    private String mCurrentTerm;

    ExamPresenter(ExamContract.View view) {
        mView = view;
        mModel = new ExamModel(view.getContext());
    }

    @Override
    public void onStart() {
//        mView.setYearTermOptions(yearIndex, termIndex);
        mCompDisposable.add(mModel
                .getYearTermList()
                .doOnNext(map -> {
                    years = map.get("xnd");
                    terms = map.get("xqd");
                })
                .flatMap(map -> {
                    Map<String, String> map2 = mModel.getYearTerm();
                    mCurrentYear = map2.get("xnd");
                    mCurrentTerm = map2.get("xqd");
                    return mModel.getExamsFromDB(mCurrentYear, mCurrentTerm);
                })
                .flatMap(exams -> {
                    if (exams.isEmpty()) {
                        return mModel.getExamsFromNet(mCurrentYear, mCurrentTerm)
                                .map(Response::getBody)
                                .retryWhen(this::ensureTokenAlive)
                                .doOnNext(list -> mModel.save(list));
                    } else {
                        return Observable.just(exams);
                    }
                })
                .compose(RxUtils.ioToMainScheduler())
                .doOnSubscribe(d -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setYearTermData(years, terms);
                    mView.setYearTermOptions(years.indexOf(mCurrentYear), terms.indexOf(mCurrentTerm));
                    mView.setExamAdapterData(list);
                }, this::onError)
        );
    }

    @Override
    public void update() {
        mCompDisposable.add(mModel.getExamsFromNet(mCurrentYear, mCurrentTerm)
                .map(Response::getBody)
                .retryWhen(this::ensureTokenAlive)
                .doOnNext(list -> mModel.save(list))
                .compose(RxUtils.ioToMainScheduler())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setExamAdapterData(list);
                }, this::onError)
        );
    }

    @Override
    public void switchYearTerm(int op1, int op2) {
        mCurrentYear = years.get(op1);
        mCurrentTerm = terms.get(op2);
        mCompDisposable.add(mModel
                .getExamsFromDB(mCurrentYear, mCurrentTerm)
                .flatMap(exams -> {
                    if (exams.isEmpty()) {
                        return mModel.getExamsFromNet(mCurrentYear, mCurrentTerm)
                                .map(Response::getBody)
                                .retryWhen(this::ensureTokenAlive)
                                .doOnNext(list -> mModel.save(list));
                    } else {
                        return Observable.just(exams);
                    }
                })
                .compose(RxUtils.ioToMainScheduler())
                .doOnSubscribe(d -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setExamAdapterData(list);
                }, this::onError)
        );
    }
}
