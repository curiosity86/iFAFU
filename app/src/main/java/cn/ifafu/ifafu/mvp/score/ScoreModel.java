package cn.ifafu.ifafu.mvp.score;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZhengFang;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.parser.ScoreParser;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;

class ScoreModel extends BaseZFModel implements ScoreContract.Model {

    ScoreModel(Context context) {
        super(context);
    }

    @Override
    public Observable<Response<List<Score>>> getScoresFromNet(String year, String term) {
        User user = repository.getUser();
        String url = School.getUrl(ZhengFang.SCORE, user);
        return initParams(url, School.getUrl(ZhengFang.MAIN, user))
                .flatMap(params -> {
                    if (user.getSchoolCode() == School.FAFU_JS) {
                        params.put("ddlXN", year);
                        params.put("ddlXQ", term);
                        params.put("ddl_kcxz", "");
                        if (year.equals("全部") && term.equals("全部")) {
                            params.put("btn_zcj", "����ɼ�");
                        } else if (term.equals("全部")) {
                            params.put("btn_xn", "ѧ�ڳɼ�");
                        } else {
                            params.put("btn_xq", "ѧ�ڳɼ�");
                        }
                    } else if (user.getSchoolCode() == School.FAFU) {
                        params.put("ddlxn", year);
                        params.put("ddlxq", term);
                        params.put("btnCx", " ��  ѯ ");
                    }
                    return APIManager.getZhengFangAPI()
                            .getInfo(url, url, params)
                            .compose(new ScoreParser(user))
                            .map(Response::success);
                });
    }

    @Override
    public Observable<Response<List<Score>>> getScoresFromNet() {
        User user = repository.getUser();
        String url = School.getUrl(ZhengFang.SCORE, user);
        return initParams(url, School.getUrl(ZhengFang.MAIN, user))
                .flatMap(params -> {
                    switch (user.getSchoolCode()) {
                        case School.FAFU:
                            params.put("ddlxn", "全部");
                            params.put("ddlxq", "全部");
                            params.put("btnCx", " ��  ѯ ");
                            break;
                        case School.FAFU_JS:
                            params.put("ddlXN", "");
                            params.put("ddlXQ", "");
                            params.put("ddl_kcxz", "");
                            params.put("btn_zcj", "����ɼ�");
                            break;
                    }
                    return APIManager.getZhengFangAPI()
                            .getInfo(url, url, params)
                            .compose(new ScoreParser(user))
                            .map(Response::success);
                });
    }

    @Override
    public List<Score> getScoresFromDB(String year, String term) {
        if (year.equals("全部") && term.equals("全部")) {
            return repository.getAllScores();
        } else if (year.equals("全部")) {
            return repository.getScoresByTerm(term);
        } else if (term.equals("全部")) {
            return repository.getScoresByYear(year);
        } else {
            return repository.getScores(year, term);
        }
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
            if (repository.getUser().getSchoolCode() == School.FAFU) {
                yearList.add("全部");
            }
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
        repository.saveScore(list);
    }

    @Override
    public void delete(String year, String term) {
        List<Score> scores = repository.getScores(year, term);
        repository.deleteScore(scores);
    }

    @Override
    public void deleteAllOnlineCourse() {
        repository.deleteAllOnlineCourse();
    }
}
