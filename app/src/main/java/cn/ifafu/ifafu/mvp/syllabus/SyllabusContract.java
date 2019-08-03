package cn.ifafu.ifafu.mvp.syllabus;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.IZFModel;
import cn.ifafu.ifafu.mvp.base.IZFPresenter;
import cn.woolsen.android.mvp.i.IView;
import cn.woolsen.android.view.syllabus.data.DayOfWeek;
import io.reactivex.Observable;

class SyllabusContract {

    interface Presenter extends IZFPresenter {

        /**
         * 更新课程表
         */
        void updateSyllabus();
    }

    interface Model extends IZFModel {

        String[] getCourseBeginTime();

        /**
         * @return 课程表列数
         */
        int getRowCount();

        /**
         * 从数据库获取课程
         * @return List
         */
        Observable<List<Course>> getAllCoursesFromDB();

        /**
         * 获取课表网页信息
         * @return Html
         */
        Observable<List<Course>> getCoursesFromNet();

        /**
         * 保存课程
         */
        void saveCourses(List<Course> courses);

        /**
         * 删除数据库中的网络获取的课程
         */
        void clearOnlineCourses();
    }

    interface View extends IView {
        /**
         * 设置课程表行数
         * @param count 课程表行数
         */
        void setSyllabusRowCount(int count);

        /**
         * 设置上课时间
         * @param times 上课时间
         */
        void setCourseBeginTime(String[] times);

        /**
         * 设置角落Text
         */
        void setCornerText(String cornerText);

        /**
         * 设置当前周
         * @param week
         */
        void setCurrentWeek(int week);

        void setSyllabusDate(List<Course> courses);

        void redrawSyllabus();

        void setFirstDayOfWeek(@DayOfWeek int firstDayOfWeek);
    }
}
