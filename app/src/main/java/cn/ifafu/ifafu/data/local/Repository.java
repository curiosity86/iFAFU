package cn.ifafu.ifafu.data.local;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.ElecCookie;
import cn.ifafu.ifafu.data.entity.ElecQuery;
import cn.ifafu.ifafu.data.entity.ElecUser;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.Setting;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.data.entity.Token;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.YearTerm;
import kotlin.Pair;

public interface Repository {

    User getLoginUser();

    List<User> getAllUser();

    void saveUser(User user);

    void saveLoginUser(User user);

    void deleteUser(User user);

    List<Course> getAllCourses();

    List<Course> getCourses(boolean local);

    Course getCourseById(long id);

    void saveCourse(Course course);

    void saveCourse(List<Course> courses);

    void deleteCourse(List<Course> courses);

    void deleteCourse(Course course);

    void deleteAllOnlineCourse();

    SyllabusSetting getSyllabusSetting();

    void saveSyllabusSetting(SyllabusSetting syllabusSetting);

    List<Score> getAllScores();

    List<Score> getScoresByYear(String year);

    List<Score> getScoresByTerm(String term);

    Score getScoreById(long id);

    List<Score> getScores(String year, String term);

    void deleteScore(String year, String term);

    void deleteScore(List<Score> scores);

    void deleteAllScore();

    void saveScore(List<Score> scores);

    List<Exam> getAllExams();

    List<Exam> getExams(String year, String term);

    void saveExam(List<Exam> exams);

    Token getToken(String account);

    void saveToken(Token token);

    void clearAllData();

    YearTerm getYearTermList();

    Pair<String, String> getYearTerm();

    //电费查询
    ElecQuery getElecQuery();

    void saveElecQuery(ElecQuery elecQuery);

    ElecCookie getElecCookie();

    void saveElecCookie(ElecCookie cookie);

    ElecUser getElecUser();

    void saveElecUser(ElecUser elecUser);

    Setting getSetting();

    void saveSetting(Setting setting);
}
