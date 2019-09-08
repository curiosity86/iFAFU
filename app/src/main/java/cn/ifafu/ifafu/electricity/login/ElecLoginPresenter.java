package cn.ifafu.ifafu.electricity.login;

import android.content.Intent;

import com.alibaba.fastjson.JSONObject;

import cn.ifafu.ifafu.data.local.RepositoryImpl;
import cn.ifafu.ifafu.electricity.data.UserMe;
import cn.ifafu.ifafu.electricity.main.ElecMainActivity;
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
        UserMe user = mModel.getUserMe();
        if (user != null) {
            mView.setSnoEtText(user.getSno());
            mView.setPasswordText(user.getPassword());
        } else {
            String account = RepositoryImpl.getInstance().getUser().getAccount();
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
        String sno = mView.getSNoEditable().toString();
        String password = mView.getPasswordEditable().toString();
        String verify = mView.getVerifyEditable().toString();
        Disposable d = mModel.login(sno, password, verify)
                .compose(RxUtils.ioToMain())
                .doOnSubscribe( disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading())
                .subscribe(s -> {
                    JSONObject jo = JSONObject.parseObject(s);
                    if (jo.getBoolean("IsSucceed")) {
                        mView.showMessage("登录成功");
                        UserMe user = new UserMe();
                        user.setAccount(jo.getString("Obj"));
                        JSONObject obj2 = jo.getJSONObject("Obj2");
                        user.setSno(obj2.getString("SNO"));
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
