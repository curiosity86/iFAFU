package cn.ifafu.ifafu.data.local;

import androidx.annotation.Nullable;

import java.util.List;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.dao.CourseDao;
import cn.ifafu.ifafu.dao.DaoSession;
import cn.ifafu.ifafu.dao.ExamDao;
import cn.ifafu.ifafu.dao.ScoreDao;
import cn.ifafu.ifafu.dao.SyllabusSettingDao;
import cn.ifafu.ifafu.dao.TokenDao;
import cn.ifafu.ifafu.dao.UserDao;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.data.entity.Token;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.local.i.Repository;
import cn.ifafu.ifafu.util.SPUtils;

public class RepositoryImpl implements Repository {

    private UserDao userDao;
    private TokenDao tokenDao;
    private CourseDao courseDao;
    private ScoreDao scoreDao;
    private ExamDao examDao;
    private SyllabusSettingDao syllabusSettingDao;

    private User user;

    private static Repository INSTANCE;

    private RepositoryImpl() {
        DaoSession daoSession = DaoManager.getInstance().getDaoSession();
        userDao = daoSession.getUserDao();
        tokenDao = daoSession.getTokenDao();
        courseDao = daoSession.getCourseDao();
        scoreDao = daoSession.getScoreDao();
        examDao = daoSession.getExamDao();
        syllabusSettingDao = daoSession.getSyllabusSettingDao();
    }

    public static Repository getInstance() {
        if (INSTANCE == null) {
            synchronized (RepositoryImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RepositoryImpl();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public User getUser() {
        if (user == null) {
            String account = SPUtils.get(Constant.SP_USER_INFO).getString("account");
            user = userDao.load(account);
        }
        return user;
    }

    @Override
    public void saveUser(User user) {
        this.user = user;
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.getAccount());
        userDao.insertOrReplace(user);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDao.queryBuilder()
                .where(CourseDao.Properties.Account.eq(getUser().getAccount()))
                .list();
    }

    @Override
    public List<Course> getCourses(boolean local) {
        return courseDao.queryBuilder()
                .where(CourseDao.Properties.Account.eq(getUser().getAccount()),
                        CourseDao.Properties.Local.eq(local))
                .list();
    }

    @Nullable
    @Override
    public Course getCourseById(long id) {
        return courseDao.load(id);
    }

    @Override
    public void saveCourse(Course course) {
        courseDao.insertOrReplace(course);
    }

    @Override
    public void saveCourse(List<Course> courses) {
        courseDao.insertOrReplaceInTx(courses);
    }

    @Override
    public void deleteCourse(List<Course> courses) {
        courseDao.deleteInTx(courses);
    }

    @Override
    public void deleteCourse(Course course) {
        courseDao.delete(course);
    }

    @Override
    public SyllabusSetting getSyllabusSetting() {
        return syllabusSettingDao.load(getUser().getAccount());
    }

    @Override
    public void saveSyllabusSetting(SyllabusSetting syllabusSetting) {
        syllabusSettingDao.insertOrReplace(syllabusSetting);
    }

    @Override
    public List<Score> getAllScores() {
        return scoreDao.queryBuilder()
                .where(ScoreDao.Properties.Account.eq(getUser().getAccount()))
                .list();
    }

    @Override
    public List<Score> getScores(String year) {
        return scoreDao.queryBuilder()
                .where(ScoreDao.Properties.Account.eq(getUser().getAccount()),
                        ScoreDao.Properties.Year.eq(year))
                .list();
    }

    @Override
    public List<Score> getScores(String year, String term) {
        return scoreDao.queryBuilder()
                .where(ScoreDao.Properties.Account.eq(getUser().getAccount()),
                        ScoreDao.Properties.Year.eq(year),
                        ScoreDao.Properties.Term.eq(term))
                .list();
    }

    @Override
    public void deleteScore(List<Score> scores) {
        scoreDao.deleteInTx(scores);
    }

    @Override
    public void saveScore(List<Score> scores) {
        scoreDao.insertOrReplaceInTx(scores);
    }

    @Override
    public List<Exam> getAllExams() {
        return examDao.queryBuilder()
                .where(ExamDao.Properties.Account.eq(getUser().getAccount()))
                .list();
    }

    @Override
    public List<Exam> getExams(String year, String term) {
        return examDao.queryBuilder()
                .where(ExamDao.Properties.Account.eq(getUser().getAccount()),
                        ExamDao.Properties.Year.eq(year),
                        ExamDao.Properties.Term.eq(term))
                .list();
    }

    @Override
    public void saveExam(List<Exam> exams) {
        examDao.insertOrReplaceInTx(exams);
    }

    @Override
    public Token getToken(String account) {
        return tokenDao.load(account);
    }

    @Override
    public void saveToken(Token token) {
        tokenDao.insertOrReplace(token);
    }
}
