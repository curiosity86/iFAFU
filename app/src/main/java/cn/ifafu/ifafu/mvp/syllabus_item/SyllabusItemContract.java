package cn.ifafu.ifafu.mvp.syllabus_item;

import java.util.Set;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.i.IModel;
import cn.ifafu.ifafu.mvp.base.i.IPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;

class SyllabusItemContract {

    interface View extends IView {

        void setTimeOPVSelect(int op1, int op2, int op3);

        void isEditMode(boolean isEditMode);

        Set<Integer> getWeekData();

        String getNameText();

        String getAddressText();

        String getTeacherText();

        void setNameText(String name);

        void setAddressText(String address);

        void setTeacherText(String teacher);

        void setWeekData(Set<Integer> weekData);
    }

    interface Presenter extends IPresenter {
        void onSave();

        void onEdit();

        void onDelete();

        void onFinish();

        void onTimeSelect(int options1, int options2, int options3);
    }

    interface Model extends IModel {
        void save(Course course);

        void delete(Course course);

        Course getCourseById(long id);
    }

}
