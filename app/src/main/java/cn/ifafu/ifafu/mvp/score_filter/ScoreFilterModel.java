package cn.ifafu.ifafu.mvp.score_filter;

import android.content.Context;

import java.util.List;

import cn.ifafu.ifafu.dao.ScoreDao;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.base.BaseModel;
import io.reactivex.Observable;

public class ScoreFilterModel extends BaseModel implements ScoreFilterConstant.Model {

    private ScoreDao scoreDao = DaoManager.getInstance().getDaoSession().getScoreDao();

    ScoreFilterModel(Context context) {
        super(context);
    }

    @Override
    public Observable<List<Score>> getScoresFromDB(String year, String term) {
        return Observable.fromCallable(() -> {
            if (year.equals("全部") && term.equals("全部")) {
                return repository.getAllScores();
            } else if (year.equals("全部")) {
                return repository.getScoresByTerm(term);
            } else if (term.equals("全部")) {
                return repository.getScoresByYear(year);
            } else {
                return repository.getScores(year, term);
            }
        });
    }

    @Override
    public void save(Score score) {
        scoreDao.insertOrReplace(score);
    }

}
