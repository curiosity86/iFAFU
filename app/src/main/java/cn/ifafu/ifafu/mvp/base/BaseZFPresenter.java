package cn.ifafu.ifafu.mvp.base;

import android.content.Intent;

import cn.ifafu.ifafu.data.exception.LoginInfoErrorException;
import cn.ifafu.ifafu.mvp.base.i.IView;
import cn.ifafu.ifafu.mvp.base.i.IZFModel;
import cn.ifafu.ifafu.mvp.base.i.IZFPresenter;
import cn.ifafu.ifafu.mvp.login.LoginActivity;

public abstract class BaseZFPresenter<V extends IView, M extends IZFModel> extends BasePresenter<V, M> implements IZFPresenter {

    public BaseZFPresenter(V view) {
        super(view);
    }

    public BaseZFPresenter(V view, M model) {
        super(view, model);
    }

    @Override
    protected void onError(Throwable throwable) {
        if (throwable instanceof LoginInfoErrorException) {
            mView.openActivity(new Intent(mView.getContext(), LoginActivity.class));
            return;
        }
        super.onError(throwable);
    }
}
