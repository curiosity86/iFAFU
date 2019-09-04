package cn.ifafu.ifafu.mvp.syllabus;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZhengFang;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.parser.SyllabusParser;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;

public class SyllabusModel extends BaseZFModel implements SyllabusContract.Model {

    private final User user = repository.getUser();

    public SyllabusModel(Context context) {
        super(context);
    }

    @Override
    public SyllabusSetting getSyllabusSetting() {
        SyllabusSetting setting = repository.getSyllabusSetting();
        if (setting == null) {
            setting = new SyllabusSetting(repository.getUser().getAccount());
            repository.saveSyllabusSetting(setting);
        }
        return setting;
    }

    @Override
    public int getCurrentWeek() {
        try {
            SyllabusSetting setting = getSyllabusSetting();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date firstStudyDate = format.parse(setting.getOpeningDay());
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(setting.getFirstDayOfWeek());
            int currentYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            calendar.setTime(firstStudyDate);
            int firstYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
            int nowWeek = currentYearWeek - firstYearWeek + 1;
            return nowWeek > 0? nowWeek: -1;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public List<Course> getAllCoursesFromDB() {
        return repository.getAllCourses();
    }

    @Override
    public Observable<List<Course>> getCoursesFromNet() {
        String url = School.getUrl(ZhengFang.SYLLABUS, user);
        String referer = School.getUrl(ZhengFang.MAIN, user);
        return APIManager.getZhengFangAPI()
                .getInfo(url, referer, Collections.emptyMap())
                .compose(new SyllabusParser(user));
    }

    @Override
    public void saveCourses(List<Course> courses) {
        repository.saveCourse(courses);
    }

    @Override
    public void clearOnlineCourses() {
        List<Course> courses = repository.getCourses(false);
        repository.deleteCourse(courses);
    }

    @Override
    public void deleteCourse(Course course) {
        repository.deleteCourse(course);
    }

    @Override
    public List<Course> getLocalCoursesFromDB() {
        return repository.getCourses(true);
    }

    @Override
    public List<Course> getCoursesFromDB(int week, int weekday) {
        List<Course> todayCourses = new ArrayList<>();
        for (Course course : getAllCoursesFromDB()) {
            if (course.getWeekSet().contains(week) && course.getWeekday() == weekday) {
                todayCourses.add(course);
            }
        }
        return todayCourses;
    }

}
