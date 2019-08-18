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
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.data.http.parser.ExamParser;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;

class ExamModel extends BaseZFModel implements ExamContract.Model {

    private ExamDao examDao;

    private final User user = getUser();

    ExamModel(Context context) {
        super(context);
        examDao = DaoManager.getInstance().getDaoSession().getExamDao();
    }

    @Override
    public Observable<Response<List<Exam>>> getExamsFromNet(String year, String term) {
        String url = School.getUrl(ZFUrl.EXAM, user);
        return base(url, School.getUrl(ZFUrl.MAIN, user))
                .flatMap(params -> {
                    params.put("xnd", year);
                    params.put("xqd", term);
                    return zhengFang.getInfo(url, url, params)
                            .compose(new ExamParser());
                });
    }

    @Override
    public Observable<List<Exam>> getExamsFromDB(String year, String term) {
        return Observable.fromCallable(() -> examDao.queryBuilder()
                .where(ExamDao.Properties.Year.eq(year),
                        ExamDao.Properties.Term.eq(term),
                        ExamDao.Properties.Account.eq(user.getAccount()))
                .list());
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
}
