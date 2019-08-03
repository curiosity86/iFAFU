package cn.ifafu.ifafu.mvp.add_course;

import java.util.Collection;
import java.util.List;

import cn.ifafu.ifafu.data.entity.Course;
import cn.woolsen.android.mvp.i.IModel;
import cn.woolsen.android.mvp.i.IPresenter;
import cn.woolsen.android.mvp.i.IView;

class AddCourseContract {
    interface Presenter extends IPresenter {

        /**
         * @param op1 星期
         * @param op2 开始节数
         * @param op3 截至节数
         * @param layoutId
         * @return 需要显示的文本
         */
        String onWeekSelect(int op1, int op2, int op3, int layoutId);

        /**
         * @param op1 星期
         * @param op2 开始节数
         * @param op3 截至节数
         * @param layoutId
         * @return 需要显示的文本
         */
        String onTimeSelect(int op1, int op2, int op3, int layoutId);

        void onAdd();

        void onDelete(int layoutId);

        void onSave();
    }

    interface Model extends IModel {
        void save(Course course);

        void save(Collection<Course> courses);

        String getAccount();
    }

    interface View extends IView {
        /**
         * 设置上课时间选择器选项
         * @param op1 星期
         * @param op2 开始节数
         * @param op3 截至节数
         */
        void setTimeOPVOptions(List<String> op1, List<String> op2, List<String> op3);

        /**
         * 设置Week选择器选项
         * @param op1 周数类型 单周或者双周
         * @param op2 起始周
         * @param op3 截止周
         */
        void setWeekOPVOptions(List<String> op1, List<String> op2, List<String> op3);

        String getNameText();

        String getTeacherText();

        String getAddressText(int layoutId);

        /**
         * @param  layoutId
         */
        void addTimeView(int layoutId);
    }
}
