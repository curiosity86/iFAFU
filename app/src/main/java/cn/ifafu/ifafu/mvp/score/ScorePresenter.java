package cn.ifafu.ifafu.mvp.score;

import android.annotation.SuppressLint;
import android.content.Intent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
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
                    update(true);
                    mView.setYearTermTitle(mCurrentYear, mCurrentTerm);
                    mView.setYearTermData(years, terms);
                    mView.setYearTermOptions(years.indexOf(mCurrentYear), terms.indexOf(mCurrentTerm));
                }, this::onError)
        );
    }

    @Override
    public void update() {
        update(false);
    }

    private void update(boolean showMessage) {
        mCompDisposable.add(mModel
                .getScoresFromNet(mCurrentYear, mCurrentTerm)
                .map(Response::getBody)
                .doOnNext(list -> {
                    mModel.delete(mCurrentYear, mCurrentTerm);
                    mModel.save(list);
                })
                .retryWhen(this::ensureTokenAlive)
                .doOnNext(list -> mModel.save(list))
                .compose(RxUtils.singleToMain())
                .doOnSubscribe(d -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    calcIES(list);
                    calcGPA(list);
                    mView.setRvScoreData(list);
                    if (!showMessage) {
                        mView.showMessage(R.string.score_refresh_successful);
                    }
                }, this::onError)
        );
    }

    @Override
    public void switchYearTerm(int options1, int options2) {
        mCurrentYear = years.get(options1);
        mCurrentTerm = terms.get(options2);
        mCompDisposable.add(Observable
                .fromCallable(() ->
                        mModel.getScoresFromDB(mCurrentYear, mCurrentTerm)
                )
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
                    calcGPA(list);
                    mView.setIESText(String.valueOf(list.size()), "门");
                    mView.setRvScoreData(list);
                }, this::onError)
        );
    }

    @Override
    public void openFilterActivity() {
        Intent intent = new Intent(mView.getActivity(), ScoreFilterActivity.class);
        intent.putExtra("year", mCurrentYear);
        intent.putExtra("term", mCurrentTerm);
        mView.getActivity().startActivityForResult(intent, Constant.SCORE_FILTER_ACTIVITY);
    }

    private void calcIES(List<Score> scores) {
        mCompDisposable.add(Observable
                .just(scores)
                .map(list -> {
                    float totalScore = 0;
                    float totalCredit = 0;
                    float totalMinus = 0;
                    for (Score score : list) {
                        if (score.getIsIESItem()) {
                            totalScore += score.getScore() * score.getCredit();
                            totalCredit += score.getCredit();
                            if (score.getScore() < 60 && score.getMakeupScore() < 60) {
                                totalMinus += score.getCredit();
                            }
                        }
                    }
                    Map<String, String> retMap = new HashMap<>();
                    if (totalCredit == 0) {
                        retMap.put("big", "0");
                        retMap.put("little", "分");
                    } else {
                        @SuppressLint("DefaultLocale")
                        String result = String.format("%.2f", totalScore / totalCredit - totalMinus);
                        result = GlobalLib.trimZero(result);
                        int index = result.indexOf('.');
                        if (index == -1) {
                            retMap.put("big", result);
                            retMap.put("little", "分");
                        } else {
                            retMap.put("big", result.substring(0, index));
                            retMap.put("little", result.substring(index) + "分");
                        }
                    }
                    return retMap;
                })
                .compose(RxUtils.computationToMain())
                .subscribe(map -> {
                    mView.setIESText(map.get("big"), map.get("little"));
                }, this::onError)
        );
    }

    private void calcGPA(List<Score> list) {
        float totalGPA = 0;
        for (Score score : list) {
            totalGPA += score.getGpa();
        }
        @SuppressLint("DefaultLocale")
        String gpa = GlobalLib.trimZero(String.format("%.2f", totalGPA));
        mView.setGPAText(mView.getContext().getString(R.string.score_gpa, gpa));
    }

    @Override
    public void updateIES() {
        calcIES(mModel.getScoresFromDB(mCurrentYear, mCurrentTerm));
    }

}
