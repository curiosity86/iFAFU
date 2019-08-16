package cn.ifafu.ifafu.mvp.exam;

import android.content.Context;

import java.util.List;

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
                .where(ExamDao.Properties.Year.eq(year), ExamDao.Properties.Term.eq(term))
                .list());
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
