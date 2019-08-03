package cn.ifafu.ifafu.app;

import cn.ifafu.ifafu.data.dao.DaoManager;
import cn.ifafu.ifafu.data.entity.User;
import cn.woolsen.android.mvp.BaseApplication;
import cn.woolsen.android.uitl.SPUtils;

public class IFAFU extends BaseApplication {

    private static User user;

    public static User getUser() {
        if (user == null && SPUtils.get(Constant.SP_USER_INFO).contain("account")) {
            user = updateUser();
        }
        return user;
    }

    public static void setUser(User user) {
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.getAccount());
        IFAFU.user = user;
    }

    public static User updateUser() {
        String account = SPUtils.get(Constant.SP_USER_INFO).getString("account");
        user = DaoManager.getInstance().getDaoSession().getUserDao().load(account);
        return user;
    }

}
