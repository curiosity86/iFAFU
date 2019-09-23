package cn.ifafu.ifafu.data.local;

import android.content.Context;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.dao.CourseDao;
import cn.ifafu.ifafu.dao.DaoMaster;
import cn.ifafu.ifafu.dao.DaoSession;
import cn.ifafu.ifafu.dao.ExamDao;
import cn.ifafu.ifafu.dao.ScoreDao;
import cn.ifafu.ifafu.dao.UserDao;
import cn.ifafu.ifafu.base.BaseApplication;

public class DaoManager {

    private DaoSession mDaoSession;

    private static DaoManager INSTANCE;

    public static DaoManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DaoManager(BaseApplication.getAppContext());
        }
        return INSTANCE;
    }

    private DaoManager(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Constant.DB_NAME, null) {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                }, UserDao.class, ScoreDao.class, ExamDao.class, CourseDao.class);
            }
        };
        mDaoSession = new DaoMaster(helper.getWritableDb()).newSession();
    }

    /**
     * 向外提供DaoSession，获取相应DAO类进行增删改查
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}
