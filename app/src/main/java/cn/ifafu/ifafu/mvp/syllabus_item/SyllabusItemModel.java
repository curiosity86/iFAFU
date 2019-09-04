package cn.ifafu.ifafu.mvp.syllabus_item;

import android.content.Context;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.mvp.base.BaseModel;

class SyllabusItemModel extends BaseModel implements SyllabusItemContract.Model {

    SyllabusItemModel(Context context) {
        super(context);
    }

    @Override
    public SyllabusSetting getSyllabusSetting() {
        return repository.getSyllabusSetting();
    }

    @Override
    public void save(Course course) {
        repository.saveCourse(course);
    }

    @Override
    public void delete(Course course) {
        repository.deleteCourse(course);
    }

    @Override
    public Course getCourseById(long id) {
        return repository.getCourseById(id);
    }
}
