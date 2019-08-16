package cn.ifafu.ifafu.mvp.exam;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

class ExamPresenter extends BaseZFPresenter<ExamContract.View, ExamContract.Model>
        implements ExamContract.Presenter {

    private String year = "2018-2019";
    private String term = "2";

    ExamPresenter(ExamContract.View view) {
        mView = view;
        mModel = new ExamModel(view.getContext());
    }

    @Override
    public void onStart() {
        mCompDisposable.add(mModel
                .getExamsFromDB(year, term)
                .flatMap(exams -> {
                    if (exams.isEmpty()) {
                        return mModel.getExamsFromNet(year, term)
                                .map(Response::getBody);
                    } else {
                        return Observable.just(exams);
                    }
                })
                .retryWhen(this::ensureTokenAlive)
                .doOnNext(list -> mModel.save(list))
                .compose(RxUtils.ioToMainScheduler())
                .subscribe(list -> {
                    mView.setExamAdapterData(list);
                }, this::onError)
        );
    }


    @Override
    public void update() {
        mCompDisposable.add(mModel.getExamsFromNet(year, term)
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
}
