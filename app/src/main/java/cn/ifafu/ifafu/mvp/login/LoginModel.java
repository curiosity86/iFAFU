package cn.ifafu.ifafu.mvp.login;

import android.content.Context;
import android.util.Log;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.dao.UserDao;
import cn.ifafu.ifafu.data.Response;
import cn.ifafu.ifafu.data.dao.DaoManager;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.http.RetrofitFactory;
import cn.ifafu.ifafu.http.parser.LoginParser;
import cn.ifafu.ifafu.http.parser.VerifyParser;
import cn.ifafu.ifafu.http.service.ZhengFangService;
import cn.ifafu.ifafu.mvp.base.BaseZFModel;
import cn.woolsen.android.uitl.RxJavaUtils;
import cn.woolsen.android.uitl.SPUtils;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

class LoginModel extends BaseZFModel implements LoginContract.Model {

    private UserDao userDao;

    LoginModel(Context context) {
        super(context);
        userDao = DaoManager.getInstance().getDaoSession().getUserDao();
    }

    @Override
    public Observable<Response<String>> login(User user) {
        return super.login(user);
    }

    @Override
    public void saveUser(User user) {
        SPUtils.get(Constant.SP_USER_INFO).putString("account", user.getAccount());
        IFAFU.updateUser();
        userDao.insertOrReplace(user);
    }

}
