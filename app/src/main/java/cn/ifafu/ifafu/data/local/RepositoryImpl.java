package cn.ifafu.ifafu.data.local;

import androidx.annotation.Nullable;

import org.greenrobot.greendao.AbstractDao;

import java.util.List;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.dao.CourseDao;
import cn.ifafu.ifafu.dao.DaoSession;
import cn.ifafu.ifafu.dao.ElecCookieDao;
import cn.ifafu.ifafu.dao.ElecQueryDao;
import cn.ifafu.ifafu.dao.ElecUserDao;
import cn.ifafu.ifafu.dao.ExamDao;
import cn.ifafu.ifafu.dao.ScoreDao;
import cn.ifafu.ifafu.dao.SyllabusSettingDao;
import cn.ifafu.ifafu.dao.TokenDao;
import cn.ifafu.ifafu.dao.UserDao;
import cn.ifafu.ifafu.data.entity.Course;
import cn.ifafu.ifafu.data.entity.ElecCookie;
import cn.ifafu.ifafu.data.entity.ElecQuery;
import cn.ifafu.ifafu.data.entity.ElecUser;
import cn.ifafu.ifafu.data.entity.Exam;
import cn.ifafu.ifafu.data.entity.Score;
import cn.ifafu.ifafu.data.entity.SyllabusSetting;
import cn.ifafu.ifafu.data.entity.Token;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.util.SPUtils;

public class RepositoryImpl implements Repository {

    private UserDao userDao;
    private TokenDao tokenDao;
    private CourseDao courseDao;
    private ScoreDao scoreDao;
    private ExamDao examDao;
    private SyllabusSettingDao syllabusSettingDao;
    private ElecQueryDao elecQueryDao;
    private ElecCookieDao elecCookieDao;
    private ElecUserDao elecUserDao;

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
        elecQueryDao = daoSession.getElecQueryDao();
        elecCookieDao = daoSession.getElecCookieDao();
        elecUserDao = daoSession.getElecUserDao();
    }

    public static Repository getInstance() {
        if (INSTANCE == null) {
            synchronized (RepositoryImpl.class) {
                if (INSTANCE == null) {
                    synchronized (RepositoryImpl.class) {
                        INSTANCE = new RepositoryImpl();
                    }
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public User getLoginUser() {
        if (user == null) {
            synchronized (RepositoryImpl.class) {
                if (user == null) {
                    String account = SPUtils.get(Constant.SP_USER_INFO).getString("account");
                    user = userDao.load(account);
                }
            }
        }
        return user;
    }

    @Override
    public List<User> getAllUser() {
        return userDao.loadAll();
    }

    @Override
    public void saveUser(User user) {
        this.user = user;
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.getAccount());
        userDao.insertOrReplace(user);
    }

    @Override
    public void saveLoginUser(User user) {
        this.user = user;
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.getAccount());
    }

    @Override
    public void deleteUser(User user) {
        if (user.getAccount().equals(getLoginUser().getAccount())) {
            this.user = null;
        }
        userDao.deleteByKey(user.getAccount());
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDao.queryBuilder()
                .where(CourseDao.Properties.Account.eq(getLoginUser().getAccount()))
                .list();
    }

    @Override
    public List<Course> getCourses(boolean local) {
        return courseDao.queryBuilder()
                .where(CourseDao.Properties.Account.eq(getLoginUser().getAccount()),
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
    public void deleteAllOnlineCourse() {
        deleteCourse(getCourses(false));
    }

    @Override
    public SyllabusSetting getSyllabusSetting() {
        return syllabusSettingDao.load(getLoginUser().getAccount());
    }

    @Override
    public void saveSyllabusSetting(SyllabusSetting syllabusSetting) {
        syllabusSettingDao.insertOrReplace(syllabusSetting);
    }

    @Override
    public List<Score> getAllScores() {
        return scoreDao.queryBuilder()
                .where(ScoreDao.Properties.Account.eq(getLoginUser().getAccount()))
                .list();
    }

    @Override
    public List<Score> getScoresByYear(String year) {
        return scoreDao.queryBuilder()
                .where(ScoreDao.Properties.Account.eq(getLoginUser().getAccount()),
                        ScoreDao.Properties.Year.eq(year))
                .list();
    }

    @Override
    public List<Score> getScoresByTerm(String term) {
        return scoreDao.queryBuilder()
                .where(ScoreDao.Properties.Account.eq(getLoginUser().getAccount()),
                        ScoreDao.Properties.Term.eq(term))
                .list();
    }

    @Override
    public Score getScoreById(long id) {
        return scoreDao.load(id);
    }

    @Override
    public List<Score> getScores(String year, String term) {
        return scoreDao.queryBuilder()
                .where(ScoreDao.Properties.Account.eq(getLoginUser().getAccount()),
                        ScoreDao.Properties.Year.eq(year),
                        ScoreDao.Properties.Term.eq(term))
                .list();
    }

    @Override
    public void deleteScore(String year, String term) {
        scoreDao.deleteInTx(getScores(year, term));
    }

    @Override
    public void deleteScore(List<Score> scores) {
        scoreDao.deleteInTx(scores);
    }

    @Override
    public void deleteAllScore() {
        scoreDao.deleteInTx(getAllScores());
    }

    @Override
    public void saveScore(List<Score> scores) {
        scoreDao.insertOrReplaceInTx(scores);
    }

    @Override
    public List<Exam> getAllExams() {
        return examDao.queryBuilder()
                .where(ExamDao.Properties.Account.eq(getLoginUser().getAccount()))
                .list();
    }

    @Override
    public List<Exam> getExams(String year, String term) {
        return examDao.queryBuilder()
                .where(ExamDao.Properties.Account.eq(getLoginUser().getAccount()),
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

    @Override
    public void clearAllData() {
        for (AbstractDao<?, ?> allDao : DaoManager.getInstance().getDaoSession().getAllDaos()) {
            allDao.deleteAll();
        }
    }

    @Override
    public ElecQuery getElecQuery() {
        return elecQueryDao.load(getLoginUser().getAccount());
    }

    @Override
    public void saveElecQuery(ElecQuery elecQuery) {
        elecQueryDao.insertOrReplace(elecQuery);
    }

    @Override
    public ElecCookie getElecCookie() {
        return elecCookieDao.load(getLoginUser().getAccount());
    }

    @Override
    public void saveElecCookie(ElecCookie cookie) {
        elecCookieDao.insertOrReplace(cookie);
    }

    @Override
    public ElecUser getElecUser() {
        return elecUserDao.load(getLoginUser().getAccount());
    }

    @Override
    public void saveElecUser(ElecUser elecUser) {
        elecUserDao.insertOrReplace(elecUser);
    }
}
