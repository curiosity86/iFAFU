package cn.ifafu.ifafu.mvp.score_item;

import android.content.Context;

import cn.ifafu.ifafu.entity.Score;
import cn.ifafu.ifafu.base.BaseModel;

public class ScoreItemModel extends BaseModel implements ScoreItemConstant.Model {
    public ScoreItemModel(Context context) {
        super(context);
    }

    @Override
    public Score getScoreById(long id) {
        return repository.getScoreById(id);
    }
}
