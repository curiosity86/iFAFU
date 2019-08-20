package cn.ifafu.ifafu.mvp.syllabus;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.view.syllabus.data.DayOfWeek;
import io.reactivex.Observable;

class SyllabusContract {

    interface Presenter extends IZFPresenter {
        /**
         * 显示 完成Toast
         * 静默更新课表，不强制刷新，除非数据库数据为空
         */
        void updateSyllabusLocal();

        /**
         * 显示 加载Progress 与 完成Toast
         * 强制刷新
         */
        void updateSyllabusNet();

        /**
         * 删除课程
         */
        void onDelete(Course course);
    }

    interface Model extends IZFModel {

        String getFirstStudyDay();

        /**
         * @return 每周的首日 {@link Calendar}
         */
        int getFirstDayOfWeek();

        /**
         * @return 上课时间
         */
        int[] getCourseBeginTime();

        /**
         * @return 课程表列数
         */
        int getRowCount();

        int getCurrentWeek() throws ParseException;

        //一节课时间，单位分钟
        int getOneNodeLength();

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

        void setFirstStudyDay(String firstStudyDay);

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
