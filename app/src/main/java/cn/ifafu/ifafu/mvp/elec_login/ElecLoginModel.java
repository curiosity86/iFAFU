package cn.ifafu.ifafu.mvp.elec_login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.IOException;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.entity.ElecCookie;
import cn.ifafu.ifafu.data.entity.ElecQuery;
import cn.ifafu.ifafu.data.entity.ElecUser;
import cn.ifafu.ifafu.data.http.elec.LoginService;
import cn.ifafu.ifafu.data.http.elec.RetrofitFactory;
import cn.ifafu.ifafu.base.BaseModel;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

public class ElecLoginModel extends BaseModel implements ElecLoginContract.Model {

    private final LoginService service = RetrofitFactory.obtainService(LoginService.class, null);
    private boolean isInit = false;

    public ElecLoginModel(Context context) {
        super(context);
    }

    @Override
    public ElecUser getUser() {
        return repository.getElecUser();
    }

    @Override
    public Observable<Bitmap> verifyBitmap() {
        return Observable.create(emitter -> {
            if (!isInit) {
                isInit = true;
                service.init("0", SPUtils.get(Constant.SP_ELEC).getString("IMEI"),
                        "0").execute();
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
                    "http://cardapp.fafu.edu.cn:8088/Phone/Login?sourcetype=0&IMEI=" +
                            SPUtils.get(Constant.SP_ELEC).getString("IMEI") + "&language=0",
                    sno, new String(Base64.encode(password.getBytes(), Base64.DEFAULT)),
                    verify, "1", "1", "", "true"
            ).execute().body();
            assert responseBody != null;
            emitter.onNext(responseBody.string());
            emitter.onComplete();
        });
    }

    @Override
    public void save(ElecUser user) {
        repository.saveElecUser(user);
    }

    @Override
    public void save(ElecQuery elecQuery) {
        repository.saveElecQuery(elecQuery);
    }

    @Override
    public void save(ElecCookie elecCookie) {
        repository.saveElecCookie(elecCookie);
    }
}
