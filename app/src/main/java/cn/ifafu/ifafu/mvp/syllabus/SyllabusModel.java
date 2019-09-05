package cn.ifafu.ifafu.mvp.syllabus;

import android.content.Context;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.NextCourse;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZhengFang;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.parser.SyllabusParser;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import cn.ifafu.ifafu.util.DateUtils;
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
    public NextCourse getNextCourse() {
        NextCourse result = new NextCourse();

        SyllabusSetting setting = getSyllabusSetting();

        int currentWeek = getCurrentWeek();
        if (currentWeek <= 0) {
            result.setTitle("放假了呀！！");
            result.setResult(NextCourse.IN_HOLIDAY);
            return result;
        }
        result.setWeekText(MessageFormat.format("第{0}周", currentWeek));

        List<Course> courses = getAllCoursesFromDB();
        if (courses.isEmpty()) {
            result.setTitle("暂无课程信息");
            result.setResult(NextCourse.EMPTY_DATA);
            return result;
        }

        int currentWeekday = DateUtils.getCurrentDayOfWeek();
        List<Course> todayCourses = getCoursesFromDB(currentWeek, currentWeekday);
        Collections.sort(todayCourses, (o1, o2) -> Integer.compare(o1.getBeginNode(), o2.getBeginNode()));
        if (todayCourses.isEmpty()) {
            result.setTitle("今天没课哦~");
            result.setResult(NextCourse.NO_TODAY_COURSE);
            return result;
        }

        //计算下一节是第几节课
        int[] intTime = setting.getBeginTime();
        Calendar c = Calendar.getInstance();
        int now = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
        int nextNode = 9999;
        for (int i = 0; i < intTime.length; i++) {
            if (now < intTime[i]) {
                nextNode = i;
                break;
            }
        }
        //将课程按节数排列
        Map<Integer, Course> courseMap = new HashMap<>();
        for (Course course: todayCourses) {
            for (int i = course.getBeginNode(); i <= course.getEndNode(); i++) {
                courseMap.put(i, course);
            }
        }

        Course nextCourse = null;
        int courseNode = 0;

        for (Map.Entry<Integer, Course> e: courseMap.entrySet()) {
            if (e.getKey() > nextNode) {
                nextCourse = e.getValue();
                courseNode = e.getKey();
                break;
            }
        }
        if (nextCourse != null) {
            result.setResult(NextCourse.HAS_NEXT_COURSE);
            result.setTitle("下一节课：");
            result.setNodeText(MessageFormat.format("第{0}节", courseNode));
            result.setName(nextCourse.getName());
            result.setAddress(nextCourse.getAddress());
            int length = setting.getNodeLength();
            int intStartTime = intTime[courseNode - 1];
            int intEndTime = intTime[courseNode - 1];
            if (intEndTime % 100 + length >= 60) {
                intEndTime = intEndTime + 100 - (intEndTime % 100) + ((intEndTime % 100 + length) % 60);
            } else {
                intEndTime += 60;
            }
            String time =  String.format(Locale.CHINA, "%d:%02d-%d:%02d",
                    intStartTime / 100,
                    intStartTime % 100,
                    intEndTime / 100,
                    intEndTime % 100);
            result.setTimeText(time);
        } else {
            result.setTitle("今天的课上完啦~");
            result.setResult(NextCourse.NO_NEXT_COURSE);
        }
        return result;
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
