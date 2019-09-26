package cn.ifafu.ifafu.base;

import android.database.sqlite.SQLiteConstraintException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.base.i.IModel;
import cn.ifafu.ifafu.base.i.IPresenter;
import cn.ifafu.ifafu.base.i.IView;
import cn.ifafu.ifafu.data.exception.NoLogException;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends IView, M extends IModel> implements IPresenter {

    protected V mView;
    protected M mModel;
    protected CompositeDisposable mCompDisposable = new CompositeDisposable();

    protected final String TAG = this.getClass().getSimpleName();


    public BasePresenter(V view) {
        mView = view;
    }

    public BasePresenter(V view, M model) {
        mView = view;
        mModel = model;
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

    protected <T> ObservableTransformer<T, T> showHideLoading() {
        return upstream -> upstream.doOnSubscribe(disposable -> mView.showLoading())
                .doFinally(() -> mView.hideLoading());
    }

    protected void onError(Throwable throwable) {
        if (throwable instanceof ConnectException
                || throwable instanceof UnknownHostException) {
            mView.showMessage(R.string.net_error);
        } else if (throwable instanceof SocketTimeoutException) {
            mView.showMessage(R.string.net_socket_timeout_error);
        } else if (throwable instanceof SQLiteConstraintException) {
            mView.showMessage(R.string.net_sql_constraint_error);
        } else {
            mView.showMessage(throwable.getMessage());
        }
        if (!(throwable instanceof NoLogException)) {
            throwable.printStackTrace();
        }
    }
}
