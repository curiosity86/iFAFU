package cn.ifafu.ifafu.mvp.syllabus;

import android.content.Context;

import java.util.Calendar;
import java.util.List;

import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.dao.CourseDao;
import cn.ifafu.ifafu.data.dao.DaoManager;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.http.RetrofitManager;
import cn.ifafu.ifafu.http.parser.SyllabusParser;
import cn.ifafu.ifafu.http.service.ZhengFangService;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;

class SyllabusModel extends BaseZFModel implements SyllabusContract.Model {

    private final int firstDayOfWeek = Calendar.SUNDAY;

    private final String firstStudyDay = "2019-09-01";

    private final int mRowCount = 12;

    private final String[][] courseBeginTime = new String[][]{
            {"8:00", "8:50", "9:55", "10:45", "11:35", "14:00", "14:50", "15:50", "16:40", "18:25", "19:15", "20:05"},
            {"8:30", "9:20", "10:25", "11:15", "12:05", "14:00", "14:50", "15:45", "16:35", "18:25", "19:15", "20:05"}};

    private ZhengFangService zhengFang;

    private User user = IFAFU.getUser();

    private CourseDao courseDao = DaoManager.getInstance().getDaoSession().getCourseDao();

    SyllabusModel(Context context) {
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
    public String[] getCourseBeginTime() {
        return courseBeginTime[0];
    }

    @Override
    public int getRowCount() {
        return mRowCount;
    }

    @Override
    public List<Course> getAllCoursesFromDB() {
        return courseDao.queryBuilder()
                .where(CourseDao.Properties.Account.eq(user.getAccount()))
                .list();
    }

    @Override
    public Observable<List<Course>> getCoursesFromNet() {
        return zhengFang.getInfo("xskbcx.aspx", getReferer(user), user.getAccount(), user.getName(), "N121603")
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
}
