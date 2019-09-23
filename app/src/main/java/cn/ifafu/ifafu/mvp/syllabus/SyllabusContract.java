package cn.ifafu.ifafu.mvp.syllabus;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.Holiday;
import cn.ifafu.ifafu.data.entity.NextCourse;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.base.i.IView;
import cn.ifafu.ifafu.base.ifafu.IZFModel;
import cn.ifafu.ifafu.base.ifafu.IZFPresenter;
import cn.ifafu.ifafu.view.syllabus.CourseBase;
import io.reactivex.Observable;
import kotlin.Pair;

public class SyllabusContract {

    public interface Presenter extends IZFPresenter {

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

    public interface Model extends IZFModel {

        NextCourse getNextCourse();

        SyllabusSetting getSyllabusSetting();

        int getCurrentWeek() throws ParseException;

        List<Course> getAllCoursesFromDB();

        List<Holiday> getHolidays();

        /**
         * 获取调课方式
         * @return MutableMap<fromWeek, MutableMap<fromWeekday, Pair<toWeek, toWeekday>>>
         */
        Map<Integer, Map<Integer, Pair<Integer, Integer>>> getHolidayFromToMap();

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

        void deleteCourse(Course course);

    }

    public interface View extends IView {

        void setCurrentWeek(int nowWeek);

        void setSyllabusSetting(SyllabusSetting setting);

        void setSyllabusDate(List<List<CourseBase>> courses);

    }
}
