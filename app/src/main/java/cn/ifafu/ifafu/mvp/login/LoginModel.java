package cn.ifafu.ifafu.mvp.login;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.dao.UserDao;
import cn.ifafu.ifafu.data.Response;
import cn.ifafu.ifafu.data.dao.DaoManager;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;

class LoginModel extends BaseZFModel implements LoginContract.Model {

    private UserDao userDao;

    LoginModel(Context context) {
        super(context);
        userDao = DaoManager.getInstance().getDaoSession().getUserDao();
    }

    @NotNull
    @Override
    public Observable<Response<String>> login(@NotNull User user) {
        return super.login(user);
    }

    @Override
    public void saveUser(User user) {
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.getAccount());
        IFAFU.updateUser();
        userDao.insertOrReplace(user);
    }

}
