package cn.ifafu.ifafu.mvp.score;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.dao.ScoreDao;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.data.http.parser.ScoreParser;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;

class ScoreModel extends BaseZFModel implements ScoreContract.Model {

    private final User user = getUser();
    private final ScoreDao scoreDao;

    ScoreModel(Context context) {
        super(context);
        scoreDao = DaoManager.getInstance().getDaoSession().getScoreDao();
    }

    @Override
    public Observable<Response<List<Score>>> getScoresFromNet(String year, String term) {
        String url = School.getUrl(ZFUrl.SCORE, user);
        return base(url, School.getUrl(ZFUrl.MAIN, user))
                .flatMap(params -> {
                    params.put("ddlxn", year);
                    params.put("ddlxq", term);
                    params.put("btnCx", " ��  ѯ ");
                    return zhengFang.getInfo(url, url, params)
                            .compose(new ScoreParser())
                            .map(Response::success);
                });
    }

    @Override
    public Observable<List<Score>> getScoresFromDB(String year, String term) {
        Log.d(TAG, year + "   " + term + "    " + user.getAccount());
        return Observable.fromCallable(() ->
                scoreDao.queryBuilder()
                        .where(ScoreDao.Properties.Year.eq(year),
                                ScoreDao.Properties.Term.eq(term),
                                ScoreDao.Properties.Account.eq(user.getAccount()))
                        .list());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Observable<Map<String, List<String>>> getYearTermList() {
        return Observable.fromCallable(() -> {
            List<String> yearList = new ArrayList<>();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, 6);
            int year = c.get(Calendar.YEAR);
            for (int i = 0; i < 4; i++) {
                yearList.add(String.format("%d-%d", year - i - 1, year - i));
            }
            yearList.add("全部");
            List<String> termList = Arrays.asList("1", "2", "全部");
            Map<String, List<String>> map = new HashMap<>();
            map.put("ddlxn", yearList);
            map.put("ddlxq", termList);
            return map;
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Observable<Map<String, String>> getYearTerm() {
        return Observable.fromCallable(() -> {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, 6);
            Map<String, String> map = new HashMap<>();
            if (c.get(Calendar.MONTH) < 8) {
                map.put("ddlxq", "1");
            } else {
                map.put("ddlxq", "2");
            }
            int year = c.get(Calendar.YEAR);
            map.put("ddlxn", String.format("%d-%d", year - 1, year));
            return map;
        });
    }

    @Override
    public void save(List<Score> list) {
        for (Score score : list) {
            score.setAccount(user.getAccount());
        }
        scoreDao.insertOrReplaceInTx(list);
    }
}
