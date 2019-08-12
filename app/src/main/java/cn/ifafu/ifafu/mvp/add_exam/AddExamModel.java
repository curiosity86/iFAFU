package cn.ifafu.ifafu.mvp.add_exam;

import android.content.Context;

import cn.ifafu.ifafu.dao.ExamDao;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.mvp.base.BaseModel;

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
