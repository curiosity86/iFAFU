package cn.ifafu.ifafu.mvp.elec_login;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;

import cn.ifafu.ifafu.data.local.RepositoryImpl;
import cn.ifafu.ifafu.data.entity.ElecUser;
import cn.ifafu.ifafu.mvp.elec_main.ElecMainActivity;
import cn.ifafu.ifafu.mvp.base.BasePresenter;
import cn.ifafu.ifafu.util.RxUtils;
import io.reactivex.disposables.Disposable;

public class ElecLoginPresenter extends BasePresenter<ElecLoginContract.View, ElecLoginContract.Model>
        implements ElecLoginContract.Presenter {

    ElecLoginPresenter(ElecLoginContract.View view) {
        super(view, new ElecLoginModel(view.getContext()));
    }

    @Override
    public void onCreate() {
        ElecUser user = mModel.getUser();
        if (user != null) {
            mView.setSnoEtText(user.getAccount());
            mView.setPasswordText(user.getPassword());
        } else {
            String account = RepositoryImpl.getInstance().getLoginUser().getAccount();
            mView.setSnoEtText(account);
        }
        verify();
    }

    @Override
    public void verify() {
        Disposable d = mModel.verifyBitmap()
                .compose(RxUtils.ioToMain())
                .subscribe(bitmap -> mView.setVerifyBitmap(bitmap), this::onError);
        mCompDisposable.add(d);
    }

    @Override
    public void login() {
        String sno = mView.getSNoEditable();
        String password = mView.getPasswordEditable();
        String verify = mView.getVerifyEditable();
        Disposable d = mModel.login(sno, password, verify)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe( disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(s -> {
                    JSONObject jo = JSONObject.parseObject(s);
                    if (jo.getBoolean("IsSucceed")) {
                        mView.showMessage("登录成功");
                        ElecUser user = new ElecUser();
                        user.setXfbAccount(jo.getString("Obj"));
                        JSONObject obj2 = jo.getJSONObject("Obj2");
                        user.setAccount(obj2.getString("SNO"));
                        user.setName(obj2.getString("NAME"));
                        user.setPassword(password);
                        mModel.save(user, obj2.getString("RescouseType"));
                        mView.openActivity(new Intent(mView.getContext(), ElecMainActivity.class));
                        mView.killSelf();
                    } else {
                        if (jo.containsKey("Msg")) {
                            mView.showMessage(jo.getString("Msg"));
                        }
                        verify();
                    }
                }, throwable -> {
                    onError(throwable);
                    verify();
                });
        mCompDisposable.add(d);
    }

}
