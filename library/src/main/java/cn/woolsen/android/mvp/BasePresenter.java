package cn.woolsen.android.mvp;

import java.net.ConnectException;

import cn.woolsen.android.R;
import cn.woolsen.android.mvp.i.IModel;
import cn.woolsen.android.mvp.i.IPresenter;
import cn.woolsen.android.mvp.i.IView;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends IView, M extends IModel> implements IPresenter {

    protected V mView;
    protected M mModel;
    protected CompositeDisposable mCompDisposable = new CompositeDisposable();

    protected final String TAG = this.getClass().getSimpleName();

    public BasePresenter(V view, M model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void onStart() {

    }


    @Override
    public void onDestroy() {
        if (mModel != null) {
            mModel.onDestroy();
            mModel = null;
        }
        mCompDisposable.dispose();
        mCompDisposable = null;
        mView = null;
    }


    protected void onError(Throwable throwable) {
        if (throwable instanceof ConnectException) {
            mView.showMessage(R.string.net_error);
        }
        mView.showMessage(throwable.getMessage());
        throwable.printStackTrace();
    }
}
