package cn.ifafu.ifafu.mvp.score_item;

import android.annotation.SuppressLint;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.util.GlobalLib;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.Observable;

public class ScoreItemPresenter extends BasePresenter<ScoreItemConstant.View, ScoreItemConstant.Model>
        implements ScoreItemConstant.Presenter {

    public ScoreItemPresenter(ScoreItemConstant.View view) {
        super(view, new ScoreItemModel(view.getContext()));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onStart() {
        super.onStart();
        long id = mView.getActivity().getIntent().getLongExtra("id", 0L);
        if (id != 0L) {
            mCompDisposable.add(Observable
                    .fromCallable(() -> {
                        Score score = mModel.getScoreById(id);
                        Map<String, String> map = new LinkedHashMap<>();
                        map.put("课程名称", score.getName());
                        map.put("成绩", score.getScore() != -1 ?
                                GlobalLib.formatFloat(score.getScore(), 2) + "分" : "无");
                        map.put("学分", score.getCredit() != -1 ?
                                GlobalLib.formatFloat(score.getCredit(), 2) + "分" : "无");
                        map.put("绩点", score.getGpa() != -1 ?
                                GlobalLib.formatFloat(score.getGpa(), 2) + "分" : "无");
                        map.put("补考成绩", score.getMakeupScore() != -1 ?
                                GlobalLib.formatFloat(score.getMakeupScore(), 2) + "分" : "无");
                        map.put("课程性质", score.getNature() != null && !score.getNature().isEmpty() ?
                                score.getNature() : "无");
                        map.put("开课学院", score.getInstitute() != null && !score.getInstitute().isEmpty() ?
                                score.getInstitute() : "无");
                        map.put("学年", score.getYear());
                        map.put("学期", score.getTerm());
                        return map;
                    })
                    .compose(RxUtils.computationToMain())
                    .subscribe(data -> mView.setRvData(data), this::onError)
            );
        }
    }
}
