package cn.ifafu.ifafu.mvp.score;

import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

class ScorePresenter extends BaseZFPresenter<ScoreContract.View, ScoreContract.Model>
        implements ScoreContract.Presenter {

    private List<String> years;
    private List<String> terms;

    private String mCurrentYear;
    private String mCurrentTerm;

    ScorePresenter(ScoreContract.View view) {
        super(view, new ScoreModel(view.getContext()));
    }

    @Override
    public void onStart() {
        mCompDisposable.add(mModel
                .getYearTerm()
                .flatMap(map -> mModel.getScoresFromDB(map.get("ddlxn"), map.get("ddlxq")))
                .flatMap(scores -> {
                    if (scores.isEmpty()) {
                        return mModel.getScoresFromNet(mCurrentYear, mCurrentTerm)
                                .map(Response::getBody)
                                .retryWhen(this::ensureTokenAlive)
                                .doOnNext(list -> mModel.save(list));
                    } else {
                        return Observable.just(scores);
                    }
                })
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(d -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setScoreData(list);
                }, this::onError)
        );
        mCompDisposable.add(mModel
                .getYearTermList()
                .doOnNext(map -> {
                    years = map.get("ddlxn");
                    terms = map.get("ddlxq");
                })
                .flatMap(map -> mModel.getYearTerm())
                .doOnNext(map -> {
                    mCurrentYear = map.get("ddlxn");
                    mCurrentTerm = map.get("ddlxq");
                })
                .compose(RxUtils.ioToMain())
                .subscribe(map -> {
                    mView.setYearTermTitle(mCurrentYear, mCurrentTerm);
                    mView.setYearTermData(years, terms);
                    mView.setYearTermOptions(years.indexOf(mCurrentYear), terms.indexOf(mCurrentTerm));
                }, this::onError)
        );
    }

    @Override
    public void update() {
        mCompDisposable.add(mModel.getScoresFromNet(mCurrentYear, mCurrentTerm)
                .map(Response::getBody)
                .retryWhen(this::ensureTokenAlive)
                .doOnNext(list -> mModel.save(list))
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setScoreData(list);
                }, this::onError)
        );
    }

    @Override
    public void switchYearTerm(int options1, int options2) {
        mCurrentYear = years.get(options1);
        mCurrentTerm = terms.get(options2);
        mCompDisposable.add(mModel
                .getScoresFromDB(mCurrentYear, mCurrentTerm)
                .flatMap(scores -> {
                    if (scores.isEmpty()) {
                        return mModel.getScoresFromNet(mCurrentYear, mCurrentTerm)
                                .map(Response::getBody)
                                .retryWhen(this::ensureTokenAlive)
                                .doOnNext(list -> mModel.save(list));
                    } else {
                        return Observable.just(scores);
                    }
                })
                .compose(RxUtils.ioToMain())
                .doOnSubscribe(d -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    mView.setScoreData(list);
                }, this::onError)
        );
    }
}
