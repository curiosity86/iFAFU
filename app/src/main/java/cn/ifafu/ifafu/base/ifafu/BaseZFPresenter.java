package cn.ifafu.ifafu.base.ifafu;

import android.content.Intent;

import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.base.BasePresenter;
import cn.ifafu.ifafu.base.i.IView;
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException;
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
            Intent intent = new Intent(mView.getContext(), LoginActivity.class);
            mView.getActivity().startActivityForResult(intent, Constant.ACTIVITY_LOGIN);
            return;
        }
        super.onError(throwable);
    }
}
