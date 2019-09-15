package cn.ifafu.ifafu.mvp.base;

import android.content.Context;

import java.util.Map;

import javax.security.auth.login.LoginException;

import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.app.School;
import cn.ifafu.ifafu.data.entity.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.entity.ZhengFang;
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException;
import cn.ifafu.ifafu.data.exception.NoAuthException;
import cn.ifafu.ifafu.data.exception.VerifyException;
import cn.ifafu.ifafu.data.http.APIManager;
import cn.ifafu.ifafu.data.http.parser.LoginParamParser;
import cn.ifafu.ifafu.data.http.parser.LoginParser;
import cn.ifafu.ifafu.data.http.parser.ParamsParser;
import cn.ifafu.ifafu.data.http.parser.VerifyParser;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public abstract class BaseZFModel extends BaseModel implements IZFModel {

    public BaseZFModel(Context context) {
        super(context);
    }

    /**
     * 通过{@link Observable#retryWhen(Function)}捕捉{@link LoginException}异常后，触发登录账号
     * <p>
     * Need {@link LoginException}
     */
    protected Observable<Map<String, String>> initParams(String url, String referer) {
        return APIManager.getZhengFangAPI()
                .initParams(url, referer)
                .compose(new ParamsParser())
                .retryWhen(throwableObservable ->
                        throwableObservable.flatMap(throwable -> {
                            if (throwable instanceof NoAuthException || throwable.getMessage().contains("302")) {
                                System.out.println(IFAFU.Companion.getLoginDisposable());
                                if (IFAFU.Companion.getLoginDisposable() != null && !IFAFU.Companion.getLoginDisposable().isDisposed()) {
                                    while (!IFAFU.Companion.getLoginDisposable().isDisposed()) {
                                        Thread.sleep(100);
                                    }
                                    return Observable.just(true);
                                }
                                return reLogin();
                            } else {
                                System.out.println("Observable.error(throwable)");
                                return Observable.error(throwable);
                            }
                        })
                );
    }

    @Override
    public Observable<Response<String>> reLogin() {
//        Log.d("ReLogin", "enter");
        return Observable.just(true).flatMap(T -> {
//            Log.d("ReLogin", "reLogining");
            User user = repository.getLoginUser();
            if (user == null) {
                return Observable.empty();
            }
            String loginUrl = School.getUrl(ZhengFang.LOGIN, user);
            String verifyUrl = School.getUrl(ZhengFang.VERIFY, user);
            String mainUrl = School.getUrl(ZhengFang.MAIN, user);
            LoginParser loginParser = new LoginParser();
            return APIManager.getZhengFangAPI()
                    .getInfo(mainUrl, null)
                    .compose(loginParser)
                    .flatMap(stringResponse -> {
                        System.out.println("Observable.getInfo");
                        if (!stringResponse.isSuccess()) {
                            return innerLogin(
                                    loginUrl,
                                    verifyUrl,
                                    user.getAccount(),
                                    user.getPassword(),
                                    new LoginParamParser(),
                                    loginParser,
                                    new VerifyParser(mContext)
                            );
                        } else {
                            return Observable.just(stringResponse);
                        }
                    })
                    .map(response -> {
                        switch (response.getCode()) {
                            case Response.FAILURE:
                                throw new LoginInfoErrorException(response.getMessage());
                            case Response.ERROR:
                                throw new Exception(response.getMessage());
                            default:
                                return response;
                        }
                    })
                    .doOnNext(resp -> {
                        if (resp.isSuccess()) {
                            user.setName(resp.getBody());
                            repository.saveUser(user);
                        }
                    });
        });
    }

    protected Observable<Response<String>> innerLogin(String loginUrl,
                                                      String verifyUrl,
                                                      String account,
                                                      String password,
                                                      LoginParamParser paramsParser,
                                                      LoginParser loginParser,
                                                      VerifyParser verifyParser) {
        return APIManager.getZhengFangAPI()
                .initParams(loginUrl)
                .compose(paramsParser)
                .map(params -> {
                    params.put("txtUserName", account);
                    params.put("Textbox1", "");
                    params.put("TextBox2", password);
                    params.put("RadioButtonList1", "ѧ��");
                    params.put("Button1", "");
                    params.put("lbLanguage", "");
                    params.put("hidPdrs", "");
                    params.put("hidsc", "");
                    return params;
                })
                .flatMap(params -> APIManager.getZhengFangAPI()
                        .getCaptcha(verifyUrl)
                        .compose(verifyParser)
                        .flatMap(verify -> {
                            params.put("txtSecretCode", verify);
                            return APIManager.getZhengFangAPI()
                                    .login(loginUrl, params)
                                    .compose(loginParser);
                        })
                        .retry(10, throwable -> throwable instanceof VerifyException)
                );
    }

}
