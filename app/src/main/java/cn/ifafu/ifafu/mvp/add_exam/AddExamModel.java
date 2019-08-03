package cn.ifafu.ifafu.mvp.add_exam;

import android.content.Context;

import cn.ifafu.ifafu.dao.ExamDao;
import cn.ifafu.ifafu.data.dao.DaoManager;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.woolsen.android.mvp.BaseModel;

class AddExamModel extends BaseModel implements AddExamContract.Model {

    private ExamDao examDao;

    AddExamModel(Context context) {
        super(context);
        examDao = DaoManager.getInstance().getDaoSession().getExamDao();
    }

    @Override
    public void save(Exam exam) {
        examDao.save(exam);
    }
}
