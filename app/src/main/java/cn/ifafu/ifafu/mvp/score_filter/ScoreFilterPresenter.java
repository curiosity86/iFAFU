package cn.ifafu.ifafu.mvp.score_filter;

import android.content.Intent;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.base.BasePresenter;
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
    public void onCreate() {
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
                .fromCallable(() -> GlobalLib.formatFloat(GlobalLib.getIES(scoreList), 2))
                .compose(RxUtils.singleToMain())
                .subscribe(ies -> mView.setIES(ies), this::onError)
        );
    }

}
