package cn.ifafu.ifafu.mvp.syllabus_item;

import android.content.Context;

import cn.ifafu.ifafu.dao.CourseDao;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.BaseModel;

class SyllabusItemModel extends BaseModel implements SyllabusItemContract.Model {

    private CourseDao courseDao = DaoManager.getInstance().getDaoSession().getCourseDao();

    SyllabusItemModel(Context context) {
        super(context);
    }

    @Override
    public void save(Course course) {
        courseDao.save(course);
    }

    @Override
    public void delete(Course course) {
        courseDao.delete(course);
    }

    @Override
    public Course getCourseById(long id) {
        return courseDao.load(id);
    }
}
