package cn.ifafu.ifafu.mvp.base;

import android.content.Context;
import android.util.Log;

import java.util.Map;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.data.exception.VerifyException;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.parser.ParamsParser;
import cn.ifafu.ifafu.data.http.parser.LoginParser;
import cn.ifafu.ifafu.data.http.parser.VerifyParser;
import cn.ifafu.ifafu.data.http.service.ZhengFangService;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;

public class BaseZFModel extends BaseModel implements IZFModel {

    protected final ZhengFangService zhengFang;

    public BaseZFModel(Context context) {
        super(context);
        zhengFang = APIManager.getZhengFangAPI();
    }

    protected Observable<Map<String, String>> base(String url) {
        return zhengFang.base(url)
                .compose(new ParamsParser());
    }

    protected Observable<Map<String, String>> base(String url, String referer) {
        return zhengFang.base(url, referer)
                .compose(new ParamsParser());
    }

    @Override
    public Observable<Response<String>> login(User user) {
        String loginUrl = School.getUrl(ZFUrl.LOGIN, user);
        String verifyUrl = School.getUrl(ZFUrl.VERIFY, user);
        return base(loginUrl)
                .map(params -> {
                    params.put("txtUserName", user.getAccount());
                    params.put("Textbox1", "");
                    params.put("TextBox2", user.getPassword());
                    params.put("RadioButtonList1", "ѧ��");
                    params.put("Button1", "");
                    params.put("lbLanguage", "");
                    params.put("hidPdrs", "");
                    params.put("hidsc", "");
                    return params;
                })
                .flatMap(params -> {
                    Log.d("OnLoginTest", TAG + "  " + "onLogin");
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
