package cn.ifafu.ifafu.mvp.syllabus;

import java.text.ParseException;
import java.util.List;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter;
import io.reactivex.Observable;

class SyllabusContract {

    interface Presenter extends IZFPresenter {

        void updateSyllabusSetting();

        /**
         * 静默更新课表，不强制刷新，除非数据库数据为空（显示 Toast）
         */
        void updateSyllabusLocal();

        /**
         * 强制刷新（显示 Progress 与 Toast）
         */
        void updateSyllabusNet();

        /**
         * 删除课程
         */
        void onDelete(Course course);
    }

    interface Model extends IZFModel {

        SyllabusSetting getSyllabusSetting();

        int getCurrentWeek() throws ParseException;

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
         * 获取指定周指定星期的课程
         * @param week 周数
         * @param weekday 星期
         * @return 课程
         */
        List<Course> getCoursesFromDB(int week, int weekday);

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

        void setSyllabusSetting(SyllabusSetting setting);

        void setSyllabusDate(List<Course> courses);

        void redrawSyllabus();

    }
}
