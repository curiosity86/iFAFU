package cn.ifafu.ifafu.mvp.login;

import android.content.Context;

import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;

public class LoginModel extends BaseZFModel implements LoginContract.Model {

    LoginModel(Context context) {
        super(context);
    }

    @Override
    public void saveUser(User user) {
        repository.saveUser(user);
    }
}
