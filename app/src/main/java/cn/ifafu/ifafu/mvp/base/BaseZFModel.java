package cn.ifafu.ifafu.mvp.base;

import android.content.Context;
import android.util.Log;

import java.util.Map;

import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZFUrl;
import cn.ifafu.ifafu.data.exception.VerifyException;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.parser.LoginParser;
import cn.ifafu.ifafu.data.http.parser.ParamsParser;
import cn.ifafu.ifafu.data.http.parser.VerifyParser;
import cn.ifafu.ifafu.data.local.RepositoryImpl;
import cn.ifafu.ifafu.data.local.i.Repository;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import io.reactivex.Observable;

public class BaseZFModel extends BaseModel implements IZFModel {

    protected Repository repository;

    public BaseZFModel(Context context) {
        super(context);
        repository = RepositoryImpl.getInstance();
    }

    protected Observable<Map<String, String>> initParams(String url) {
        return APIManager.getZhengFangAPI()
                .initParams(url)
                .compose(new ParamsParser());
    }

    protected Observable<Map<String, String>> initParams(String url, String referer) {
        return APIManager.getZhengFangAPI()
                .initParams(url, referer)
                .compose(new ParamsParser());
    }

    @Override
    public Observable<Response<String>> login(User user) {
        String loginUrl = School.getUrl(ZFUrl.LOGIN, user);
        String verifyUrl = School.getUrl(ZFUrl.VERIFY, user);
        return initParams(loginUrl)
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
                    LoginParser loginParser = new LoginParser();
                    VerifyParser verifyParser = new VerifyParser(mContext);
                    return APIManager.getZhengFangAPI()
                            .getCaptcha(verifyUrl)
                            .compose(verifyParser)
                            .flatMap(verify -> {
                                params.put("txtSecretCode", verify);
                                return APIManager.getZhengFangAPI()
                                        .login(loginUrl, params)
                                        .compose(loginParser);
                            })
                            .retry(10, throwable -> throwable instanceof VerifyException);
                });
    }

    @Override
    public Observable<Response<String>> reLogin() {
        User user = repository.getUser();
        String loginUrl = School.getUrl(ZFUrl.LOGIN, user);
        String verifyUrl = School.getUrl(ZFUrl.VERIFY, user);
        String mainUrl = School.getUrl(ZFUrl.MAIN, user);
        LoginParser loginParser = new LoginParser();
        return APIManager.getZhengFangAPI()
                .getInfo(mainUrl, null)
                .compose(loginParser)
                .flatMap(stringResponse -> {
                    if (!stringResponse.isSuccess()) {
                        return initParams(loginUrl)
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
                                    return APIManager.getZhengFangAPI()
                                            .getCaptcha(verifyUrl)
                                            .compose(verifyParser)
                                            .flatMap(verify -> {
                                                params.put("txtSecretCode", verify);
                                                return APIManager.getZhengFangAPI()
                                                        .login(loginUrl, params)
                                                        .compose(loginParser);
                                            })
                                            .retry(10, throwable -> throwable instanceof VerifyException);
                                });
                    } else {
                        return Observable.just(stringResponse);
                    }
                });
    }
}
