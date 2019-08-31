package cn.ifafu.ifafu.data.local.i;

import java.util.List;

import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.data.entity.Token;
import cn.ifafu.ifafu.data.entity.User;

public interface Repository {

    User getUser();

    void saveUser(User user);

    List<Course> getCourse();

    Course getCourseById(long id);

    void saveCourse(Course course);

    void saveCourse(List<Course> courses);

    SyllabusSetting getSyllabusSetting();

    List<Score> getScore();

    List<Score> getScore(String year, String term);

    void saveScore(List<Score> scores);

    List<Exam> getExam();

    List<Exam> getExam(String year, String term);

    void saveExam(List<Exam> exams);

    Token getToken(String account);

    void saveToken(Token token);
}
