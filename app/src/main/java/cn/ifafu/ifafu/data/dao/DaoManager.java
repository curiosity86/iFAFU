package cn.ifafu.ifafu.data.dao;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.dao.DaoMaster;
import cn.ifafu.ifafu.dao.DaoSession;
import cn.woolsen.android.mvp.BaseApplication;

public class DaoManager {

    private DaoSession mDaoSession;

    private static DaoManager INSTANCE;

    public static DaoManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DaoManager(BaseApplication.getContext());
        }
        return INSTANCE;
    }

    private DaoManager(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Constant.DB_NAME, null) {
            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                super.onUpgrade(db, oldVersion, newVersion);
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
