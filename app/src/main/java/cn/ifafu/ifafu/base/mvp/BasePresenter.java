package cn.ifafu.ifafu.base.mvp;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.entity.exception.LoginInfoErrorException;
import cn.ifafu.ifafu.entity.exception.NoLogException;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends IView, M extends IModel> implements IPresenter {

    protected V mView;
    protected M mModel;
    public CompositeDisposable mCompDisposable = new CompositeDisposable();

    protected final String TAG = this.getClass().getSimpleName();

    public BasePresenter(V view) {
        mView = view;
    }

    public BasePresenter(V view, M model) {
        mView = view;
        mModel = model;
    }

    @Override
    public void onCreate() {
    }

    protected void onError(Throwable throwable) {
        if (throwable instanceof LoginInfoErrorException) {
            Intent intent = new Intent(mView.getContext(), LoginActivity.class);
            mView.getActivity().startActivityForResult(intent, Constant.ACTIVITY_LOGIN);
            return;
        } else if (throwable instanceof ConnectException || throwable instanceof UnknownHostException) {
            mView.showMessage(R.string.net_error);
        } else if (throwable instanceof SocketTimeoutException) {
            mView.showMessage(R.string.net_socket_timeout_error);
        } else if (throwable instanceof SQLiteConstraintException) {
            mView.showMessage(R.string.net_sql_constraint_error + "（错误信息：" + throwable.getMessage() + "）");
        } else {
            mView.showMessage(throwable.getMessage());
        }
        if (!(throwable instanceof NoLogException)) {
            throwable.printStackTrace();
        }
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


}
