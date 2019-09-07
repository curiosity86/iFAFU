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

    void deleteScore(List<Score> scores);

    void saveScore(List<Score> scores);

    List<Exam> getAllExams();

    List<Exam> getExams(String year, String term);

    void saveExam(List<Exam> exams);

    Token getToken(String account);

    void saveToken(Token token);

}
