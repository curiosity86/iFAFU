package cn.ifafu.ifafu.mvp.login;

import android.content.Context;

import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import io.reactivex.Observable;

public class LoginModel extends BaseZFModel implements LoginContract.Model {

    public LoginModel(Context context) {
        super(context);
    }

    @Override
    public void saveUser(User user) {
        repository.saveUser(user);
    }

    @Override
    public Observable<Response<String>> login(User user) {
        return super.login(user);
    }
}
