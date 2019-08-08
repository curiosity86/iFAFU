package cn.ifafu.ifafu.mvp.base;

import android.content.Context;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.data.Response;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.http.RetrofitManager;
import cn.ifafu.ifafu.http.parser.LoginParser;
import cn.ifafu.ifafu.http.service.ZhengFangService;
import cn.ifafu.ifafu.http.parser.VerifyParser;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class BaseZFModel extends BaseModel implements IZFModel {

    protected User mUser;

    public BaseZFModel(Context context) {
        super(context);
        mUser = IFAFU.getUser();
    }

    @Override
    public Observable<Response<String>> login(User user) {
        VerifyParser verifyParser = new VerifyParser(mContext);
        return getVerifyResp(user)
                .compose(verifyParser) // 识别验证码
                .flatMap(verify -> login(user, verify)) // 登录
                .compose(new LoginParser()) // 登录解析
                .flatMap(response -> { // 验证码错误抛异常用于重试
                    if (response.getCode() == Response.FAILURE && response.getMessage().contains("验证码")) {
                        return Observable.error(new VerifyErrorException(response.getMessage()));
                    }
                    return Observable.just(response);
                })
                .retry(10, throwable -> throwable instanceof VerifyErrorException); // 验证码错误重试，最多重试10次;
    }

    private Observable<ResponseBody> login(User user, String verify) {
        ZhengFangService zhengFang = RetrofitManager.INSTANCE.obtainService(ZhengFangService.class, getBaseUrl(user));
        return zhengFang.login(user.getAccount(), user.getPassword(), verify,
                "", "", "", "%D1%A7%C9%FA");
    }

    @Override
    public Observable<Boolean> isTokenAlive(User user) {
        return Observable
                .<Boolean>create(emitter -> {
                    ZhengFangService zhengFang = RetrofitManager.INSTANCE.obtainService(ZhengFangService.class, getBaseUrl(user));
                    ResponseBody body = zhengFang.mainHtml(user.getAccount()).execute().body();
                    if (body == null) {
                        emitter.onNext(false);
                    } else {
                        emitter.onNext(body.string().contains("欢迎您"));
                    }
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<ResponseBody> getVerifyResp(User user) {
        return Observable.create(emitter -> {
            ZhengFangService zhengFang = RetrofitManager.INSTANCE.obtainService(ZhengFangService.class, getBaseUrl(user));
            emitter.onNext(zhengFang.getCaptcha().execute().body());
            emitter.onComplete();
        });
    }

    private String getLoginReferer(User user) {
        switch (user.getSchoolCode()) {
            case Constant.FAFU:
                return Constant.URL_FAFU + user.getToken() + "default2.aspx";
            case Constant.FAFU_JS:
                return Constant.URL_FAFU_JS + "default.aspx";
            default:
                return "";
        }
    }

    protected String getReferer(User user) {
        return getBaseUrl(user) + "xs_main.aspx?xh=" + user.getAccount();
    }

    @Override
    public User getUser() {
        return mUser;
    }

    protected String getBaseUrl(User user) {
        switch (user.getSchoolCode()) {
            case Constant.FAFU:
                return Constant.URL_FAFU + user.getToken();
            case Constant.FAFU_JS:
                return Constant.URL_FAFU_JS;
            default:
                return "";
        }
    }
    
    class VerifyErrorException extends Exception {
        VerifyErrorException(String message) {
            super(message);
        }
    }
}
