package cn.ifafu.ifafu.mvp.exam;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.dao.ExamDao;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZhengFang;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.parser.ExamParser;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;

public class ExamModel extends BaseZFModel implements ExamContract.Model {

    private ExamDao examDao;

    public ExamModel(Context context) {
        super(context);
        examDao = DaoManager.getInstance().getDaoSession().getExamDao();
    }

    @Override
    public Observable<Response<List<Exam>>> getExamsFromNet(String year, String term) {
        User user = repository.getUser();
        String url = School.getUrl(ZhengFang.EXAM, user);
        return initParams(url, School.getUrl(ZhengFang.MAIN, user))
                .flatMap(params -> {
                    params.put("xnd", year);
                    params.put("xqd", term);
                    return APIManager.getZhengFangAPI()
                            .getInfo(url, url, params)
                            .compose(new ExamParser())
                            .map(listResponse -> {
                                for (Exam exam : listResponse.getBody()) {
                                    exam.setAccount(user.getAccount());
                                }
                                return listResponse;
                            });
                });
    }

    @Override
    public Observable<List<Exam>> getExamsFromDB(String year, String term) {
        return Observable.fromCallable(() -> repository.getExam(year, term));
//        return Observable.just(Collections.emptyList());
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
            List<String> termList = Arrays.asList("1", "2", "3");
            Map<String, List<String>> map = new HashMap<>();
            map.put("xnd", yearList);
            map.put("xqd", termList);
            return map;
        });
    }

    @SuppressLint("DefaultLocale")
    @Override
    public Map<String, String> getYearTerm() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 6);
        Map<String, String> map = new HashMap<>();
        if (c.get(Calendar.MONTH) < 8) {
            map.put("xqd", "1");
        } else {
            map.put("xqd", "2");
        }
        int year = c.get(Calendar.YEAR);
        map.put("xnd", String.format("%d-%d", year - 1, year));
        return map;
    }


    @Override
    public void save(List<Exam> list) {
        examDao.insertOrReplaceInTx(list);
    }

    @Override
    public void save(Exam exam) {
        examDao.insertOrReplace(exam);
    }

    @Override
    public void delete(Exam exam) {
        examDao.delete(exam);
    }

    @Override
    public void delete(long id) {
        examDao.deleteByKey(id);
    }

    @SuppressLint("DefaultLocale")
    public List<Exam> getThisTermExams() {
        Map<String, String> map = getYearTerm();
        return repository.getExam(map.get("xnd"), map.get("xqd"));
//        return examDao.queryBuilder()
//                .where(ExamDao.Properties.Year.eq(map.get("xnd")),
//                        ExamDao.Properties.Term.eq(map.get("xqd")),
//                        ExamDao.Properties.Account.eq(user.getAccount()))
//                .list();
    }
}
