package cn.ifafu.ifafu.mvp.syllabus;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import cn.ifafu.ifafu.base.i.IView;
import cn.ifafu.ifafu.base.ifafu.IZFModel;
import cn.ifafu.ifafu.base.ifafu.IZFPresenter;
import cn.ifafu.ifafu.entity.Course;
import cn.ifafu.ifafu.entity.Holiday;
import cn.ifafu.ifafu.entity.Response;
import cn.ifafu.ifafu.entity.SyllabusSetting;
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

        void cancelLoading();
    }

    public interface Model extends IZFModel {

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
         * 获取网络课表。
         *  若获取成功，1、清除数据库网络课表并保存 2、加入本地课程
         * @return Html
         */
        Observable<Response<List<Course>>> getCoursesFromNet();

    }

    public interface View extends IView {

        void setCurrentWeek(int nowWeek);

        void setSyllabusSetting(SyllabusSetting setting);

        void setSyllabusData(List<List<CourseBase>> courses);

    }
}
