package cn.ifafu.ifafu.mvp.add_course;

import android.content.Context;

import java.util.Collection;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.dao.CourseDao;
import cn.ifafu.ifafu.data.dao.DaoManager;
import cn.ifafu.ifafu.data.entity.Course;
import cn.woolsen.android.mvp.BaseModel;
import cn.woolsen.android.uitl.SPUtils;

class AddCourseModel extends BaseModel implements AddCourseContract.Model {

    private CourseDao courseDao = DaoManager.getInstance().getDaoSession().getCourseDao();

    AddCourseModel(Context context) {
        super(context);
    }

    @Override
    public void save(Course course) {
        courseDao.insertOrReplace(course);
    }

    @Override
    public void save(Collection<Course> courses) {
        courseDao.insertOrReplaceInTx(courses);
    }

    @Override
    public String getAccount() {
        return SPUtils.get(Constant.SP_USER_INFO).getString("account");
    }
}
