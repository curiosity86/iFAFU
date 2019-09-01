package cn.ifafu.ifafu.mvp.score;

import android.annotation.SuppressLint;
import android.content.Intent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.BaseZFPresenter;
import cn.ifafu.ifafu.mvp.score_filter.ScoreFilterActivity;
import cn.ifafu.ifafu.util.GlobalLib;
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
                        .doOnNext(map -> {
                            mCurrentYear = map.get("ddlxn");
                            mCurrentTerm = map.get("ddlxq");
                        })
                        .flatMap(map -> mModel.getScoresFromNet(mCurrentYear, mCurrentTerm)
                                .map(Response::getBody)
                                .retryWhen(this::ensureTokenAlive)
                                .doOnNext(list -> mModel.save(list)))
                        .compose(RxUtils.singleToMain())
                        .doOnSubscribe(d -> mView.showLoading())
                        .doFinally(() -> mView.hideLoading())
                        .subscribe(list -> {
                            calcIES(list);
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
                    calcIES(list);
                    mView.setScoreData(list);
                    mView.showMessage(R.string.score_refresh_successful);
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
                    calcIES(list);
                    mView.setScoreData(list);
                }, this::onError)
        );
    }

    @Override
    public void openFilterActivity() {
        Intent intent = new Intent(mView.getActivity(), ScoreFilterActivity.class);
        intent.putExtra("year", mCurrentYear);
        intent.putExtra("term", mCurrentTerm);
        mView.getActivity().startActivityForResult(intent, 100);
    }

    private void calcIES(List<Score> list) {
        mCompDisposable.add(Observable
                .just(list)
                .flatMap(this::ies)
                .compose(RxUtils.computationToMain())
                .subscribe(map -> {
                    mView.setIESText(map.get("big"), map.get("little"));
                    mView.setGPAText(mView.getContext().getString(R.string.score_gpa, map.get("gpa")));
                }, this::onError)
        );
    }

    @Override
    public void updateIES() {
        mCompDisposable.add(mModel
                .getScoresFromNet(mCurrentYear, mCurrentTerm)
                .map(Response::getBody)
                .flatMap(this::ies)
                .compose(RxUtils.computationToMain())
                .subscribe(map -> {
                    mView.setIESText(map.get("big"), map.get("little"));
                    mView.setGPAText(mView.getContext().getString(R.string.score_gpa, map.get("gpa")));
                }, this::onError)
        );
    }

    @SuppressLint("DefaultLocale")
    private Observable<Map<String, String>> ies(final List<Score> list) {
        return Observable.fromCallable(() -> {
            float totalScore = 0;
            float totalCredit = 0;
            float totalMinus = 0;
            float totalGPA = 0;
            for (Score score : list) {
                if (score.getIsIESItem()) {
                    totalScore += score.getScore() * score.getCredit();
                    totalCredit += score.getCredit();
                    if (score.getScore() < 60 && score.getMakeupScore() < 60) {
                        totalMinus += score.getCredit();
                    }
                }
                totalGPA += score.getGpa();
            }
            String result = String.format("%.2f",totalScore / totalCredit - totalMinus);
            result = GlobalLib.trimZero(result);
            Map<String, String> retMap = new HashMap<>();
            int index = result.indexOf('.');
            if (result.equals("NaN")) {
                retMap.put("big", "0");
                retMap.put("little", "分");
            } else if (index == -1) {
                retMap.put("big", result);
                retMap.put("little", "分");
            } else {
                retMap.put("big", result.substring(0, index));
                retMap.put("little", result.substring(index) + "分");
            }
            retMap.put("gpa", GlobalLib.trimZero(String.format("%.2f", totalGPA)));
            return retMap;
        });
    }
}
