package cn.ifafu.ifafu.mvp.score_filter;

import android.content.Context;

import java.util.List;

import cn.ifafu.ifafu.dao.ScoreDao;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.BaseModel;
import io.reactivex.Observable;

public class ScoreFilterModel extends BaseModel implements ScoreFilterConstant.Model {

    private ScoreDao scoreDao = DaoManager.getInstance().getDaoSession().getScoreDao();

    ScoreFilterModel(Context context) {
        super(context);
    }

    @Override
    public List<Score> getScoresFromDB(String year, String term, String account) {
        return scoreDao.queryBuilder()
                        .where(ScoreDao.Properties.Year.eq(year),
                                ScoreDao.Properties.Term.eq(term),
                                ScoreDao.Properties.Account.eq(account))
                        .list();
    }

    @Override
    public void save(Score score) {
        scoreDao.insertOrReplace(score);
    }

    @Override
    public void save(List<Score> scores) {
        scoreDao.insertOrReplaceInTx(scores);
    }

}
