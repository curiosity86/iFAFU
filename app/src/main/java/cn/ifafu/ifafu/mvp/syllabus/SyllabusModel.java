package cn.ifafu.ifafu.mvp.syllabus;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.dao.CourseDao;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.parser.SyllabusParser;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;

public class SyllabusModel extends BaseZFModel implements SyllabusContract.Model {

    private final int firstDayOfWeek = Calendar.SUNDAY;

    private final String firstStudyDay = "2019-09-01";

    private final int mRowCount = 12;

    private final User user = repository.getUser();

    private final int[][] courseBeginTime = new int[][]{
            {800, 850, 955, 1045, 1135, 1400, 1450, 1550, 1640, 1825, 1915, 2005},
            {830, 920, 1025, 1115, 1205, 1400, 1450, 1545, 1635, 1825, 1915, 2005}};

    private CourseDao courseDao = DaoManager.getInstance().getDaoSession().getCourseDao();

    public SyllabusModel(Context context) {
        super(context);
    }

    @Override
    public String getFirstStudyDay() {
        return firstStudyDay;
    }

    @Override
    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    @Override
    public int[] getCourseBeginTime() {
        return courseBeginTime[0];
    }

    @Override
    public int getRowCount() {
        return mRowCount;
    }

    @Override
    public int getCurrentWeek() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date firstStudyDate = format.parse(getFirstStudyDay());
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(getFirstDayOfWeek());
        int currentYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(firstStudyDate);
        int firstYearWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int nowWeek = currentYearWeek - firstYearWeek + 1;
        return nowWeek > 0? nowWeek: -1;
    }

    @Override
    public int getOneNodeLength() {
        return 45;
    }

    @Override
    public List<Course> getAllCoursesFromDB() {
        return courseDao.queryBuilder()
                .where(CourseDao.Properties.Account.eq(user.getAccount()))
                .list();
    }

    @Override
    public Observable<List<Course>> getCoursesFromNet() {
        String url = School.getUrl(ZFUrl.SYLLABUS, user);
        String referer = School.getUrl(ZFUrl.MAIN, user);
        return APIManager.getZhengFangAPI()
                .getInfo(url, referer, Collections.emptyMap())
                .compose(new SyllabusParser());
    }

    @Override
    public void saveCourses(List<Course> courses) {
        courseDao.insertOrReplaceInTx(courses);
    }

    @Override
    public void clearOnlineCourses() {
        List<Course> courses = getCourses(false);
        courseDao.deleteInTx(courses);
    }

    @Override
    public void deleteCourse(Course course) {
        courseDao.delete(course);
    }

    @Override
    public List<Course> getLocalCoursesFromDB() {
        return getCourses(true);
    }

    private List<Course> getCourses(boolean local) {
        return courseDao.queryBuilder()
                .where(CourseDao.Properties.Account.eq(user.getAccount()), CourseDao.Properties.Local.eq(local))
                .list();
    }

    /**
     * 获取指定周指定星期的课程
     * @param week 周数
     * @param weekday 星期
     * @return 课程
     */
    public List<Course> getCoursesFromDB(int week, int weekday) {
        List<Course> toWeekList = new ArrayList<>();
        for (Course course : getAllCoursesFromDB()) {
            if (course.getWeekSet().contains(week) && course.getWeekday() == weekday) {
                toWeekList.add(course);
            }
        }
        return toWeekList;
    }

}
