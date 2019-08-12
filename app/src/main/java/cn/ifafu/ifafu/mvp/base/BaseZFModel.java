package cn.ifafu.ifafu.mvp.base;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.data.http.RetrofitManager;
import cn.ifafu.ifafu.data.exception.VerifyException;
import cn.ifafu.ifafu.data.http.parser.DefaultParser;
import cn.ifafu.ifafu.data.http.parser.LoginParser;
import cn.ifafu.ifafu.data.http.parser.VerifyParser;
import cn.ifafu.ifafu.data.http.service.ZhengFangService;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;

public class BaseZFModel extends BaseModel implements IZFModel {

    protected ZhengFangService zhengFang;

    public BaseZFModel(Context context) {
        super(context);
        zhengFang = RetrofitManager.obtainService(ZhengFangService.class);
    }

    @Override
    public Observable<Response<String>> login(User user) {
        String loginUrl = School.getUrl(ZFUrl.LOGIN, user);
        String verifyUrl = School.getUrl(ZFUrl.VERIFY, user);
        return zhengFang.loginBase(loginUrl)
                .compose(new DefaultParser())
                .map(response -> {
                    Map<String, String> params = new HashMap<>();
                    params.put("txtUserName", user.getAccount());
                    params.put("Textbox1", "");
                    params.put("TextBox2", user.getPassword());
                    params.put("RadioButtonList1", "ѧ��");
                    params.put("Button1", "");
                    params.put("lbLanguage", "");
                    params.put("hidPdrs", "");
                    params.put("hidsc", "");
                    params.putAll(response.getHiddenParams());
                    return params;
                })
                .flatMap(params -> {
                    VerifyParser verifyParser = new VerifyParser(mContext);
                    LoginParser loginParser = new LoginParser();
                    return zhengFang.getCaptcha(verifyUrl)
                            .compose(verifyParser)
                            .flatMap(verify -> {
                                params.put("txtSecretCode", verify);
                                return zhengFang.login(loginUrl, params)
                                        .compose(loginParser);
                            })
                            .retry(10, throwable -> throwable instanceof VerifyException);
                });
    }

    @Override
    public User getUser() {
        String account = SPUtils.get(Constant.SP_USER_INFO).getString("account");
        return DaoManager.getInstance().getDaoSession().getUserDao().load(account);
    }
}
