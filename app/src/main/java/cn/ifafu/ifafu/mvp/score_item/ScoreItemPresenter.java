package cn.ifafu.ifafu.mvp.score_item;

import cn.ifafu.ifafu.mvp.base.BasePresenter;

public class ScoreItemPresenter extends BasePresenter<ScoreItemConstant.View, ScoreItemConstant.Model>
        implements ScoreItemConstant.Presenter {

    public ScoreItemPresenter(ScoreItemConstant.View view) {
        super(view, new ScoreItemModel(view.getContext()));
    }

}
