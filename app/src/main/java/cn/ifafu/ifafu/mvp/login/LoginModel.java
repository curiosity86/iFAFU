package cn.ifafu.ifafu.mvp.login;

import android.content.Context;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import cn.ifafu.ifafu.util.SPUtils;

public class LoginModel extends BaseZFModel implements LoginContract.Model {

    LoginModel(Context context) {
        super(context);
    }

    @Override
    public void saveUser(User user) {
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.getAccount());
        DaoManager.getInstance().getDaoSession().getUserDao().insertOrReplace(user);
    }
}
