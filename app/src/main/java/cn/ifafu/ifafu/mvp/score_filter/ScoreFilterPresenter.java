package cn.ifafu.ifafu.mvp.score_filter;

import android.annotation.SuppressLint;
import android.content.Intent;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.util.GlobalLib;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

class ScoreFilterPresenter extends BasePresenter<ScoreFilterConstant.View, ScoreFilterConstant.Model>
        implements ScoreFilterConstant.Presenter {

    private List<Score> scores;

    ScoreFilterPresenter(ScoreFilterConstant.View view) {
        super(view, new ScoreFilterModel(view.getContext()));
    }

    @Override
    public void onStart() {
        Intent intent = mView.getActivity().getIntent();
        String year = intent.getStringExtra("year");
        String term = intent.getStringExtra("term");
        mCompDisposable.add(mModel
                .getScoresFromDB(year, term)
                .doOnNext(list -> scores = list)
                .compose(RxUtils.singleToMain())
                .doOnSubscribe(d -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(list -> {
                    calcIES(scores);
                    mView.setAdapterData(list);
                }, this::onError)
        );
    }

    @Override
    public void onCheck(Score score, boolean checked) {
        score.setIsIESItem(checked);
        mModel.save(score);
        updateIES();
    }

    @Override
    public void updateIES() {
        calcIES(scores);
    }

    private void calcIES(List<Score> scoreList) {
        mCompDisposable.add(Observable
                .fromCallable(() -> {
                    if (scoreList == null || scoreList.isEmpty()) {
                        return "0";
                    }
                    float totalScore = 0;
                    float totalCredit = 0;
                    float totalMinus = 0;
                    for (Score score : scoreList) {
                        if (score.getIsIESItem()) {
                            totalScore += score.getScore() * score.getCredit();
                            totalCredit += score.getCredit();
                            if (score.getScore() < 60 && score.getMakeupScore() < 60) {
                                totalMinus += score.getCredit();
                            }
                        }
                    }
                    @SuppressLint("DefaultLocale")
                    String result = String.format("%.2f",totalScore / totalCredit - totalMinus);
                    return GlobalLib.trimZero(result);
                })
                .compose(RxUtils.singleToMain())
                .subscribe(ies -> mView.setIES(ies), this::onError)
        );
    }

}
