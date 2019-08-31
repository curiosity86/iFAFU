package cn.ifafu.ifafu.electricity.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.IOException;

import cn.ifafu.ifafu.mvp.base.BaseModel;
import cn.ifafu.ifafu.electricity.data.UserMe;
import cn.ifafu.ifafu.electricity.http.LoginService;
import cn.ifafu.ifafu.electricity.http.RetrofitFactory;
import cn.ifafu.ifafu.electricity.util.SPUtils;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class ElecLoginModel extends BaseModel implements ElecLoginContract.Model {

    private final LoginService service = RetrofitFactory.obtainService(LoginService.class, null);
    private boolean isInit = false;

    public ElecLoginModel(Context context) {
        super(context);
    }

    @Override
    public UserMe getUserMe() {
        if (SPUtils.get("UserInfo").contain("sno") && SPUtils.get("UserInfo").contain("password")) {
            UserMe user = new UserMe();
            user.setSno(SPUtils.get("UserInfo").getString("sno"));
            user.setPassword(SPUtils.get("UserInfo").getString("password"));
            return user;
        }
        return null;
    }

    @Override
    public Observable<Bitmap> verifyBitmap() {
        return Observable.create(emitter -> {
            if (!isInit) {
                isInit = true;
                service.init("0", SPUtils.get("Const").getString("IMEI"), "0").execute();
            }
            ResponseBody responseBody = service.verify(
                    String.valueOf(System.currentTimeMillis())
            ).execute().body();
            try {
                assert responseBody != null;
                byte[] bytes = responseBody.bytes();
                emitter.onNext(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            } catch (IOException e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }

    @Override
    public Observable<String> login(String sno, String password, String verify) {
        return Observable.create(emitter -> {
            ResponseBody responseBody = service.login(
                    "http://cardapp.fafu.edu.cn:8088/Phone/Login?sourcetype=0&IMEI=" + SPUtils.get("Const").getString("IMEI") + "&language=0",
                    sno, new String(Base64.encode(password.getBytes(), Base64.DEFAULT)),
                    verify, "1", "1", "", "true"
            ).execute().body();
            assert responseBody != null;
            emitter.onNext(responseBody.string());
            emitter.onComplete();
        });
    }

    @Override
    public void save(UserMe user, String rescouseType) {
        SPUtils.get("UserInfo").putString("account", user.getAccount());
        SPUtils.get("UserInfo").putString("sno", user.getSno());
        SPUtils.get("UserInfo").putString("password", user.getPassword());
        SPUtils.get("UserInfo").putString("name", user.getName());
        SPUtils.get("Cookie").putString("RescouseType", rescouseType);
    }

}
