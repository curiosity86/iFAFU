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
         * 刷新课表。若数据库为空，则自动刷新
         *
         * @param refresh   是否强制刷新
         * @param showToast 加载完是否显示Toast
         */
        void updateSyllabus(boolean refresh, boolean showToast);

        /**
         * 删除课程
         */
        void onDelete(Course course);
    }

    interface Model extends IZFModel {

        String[] getCourseBeginTime();

        /**
         * @return 课程表列数
         */
        int getRowCount();

        /**
         * 从数据库获取所有课程
         * @return List<Course>
         */
        List<Course> getAllCoursesFromDB();

        /**
         * 从数据库获取本地课程
         * @return List
         */
        List<Course> getLocalCoursesFromDB();


        /**
         * 获取网络课表。若获取成功，则清除数据库网络课表，并保存
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

        void deleteCourse(Course course);
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
