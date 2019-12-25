package cn.ifafu.ifafu.base;

import android.content.Context;

import cn.ifafu.ifafu.data.RepositoryImpl;
import cn.ifafu.ifafu.data.Repository;
import cn.ifafu.ifafu.base.i.IModel;

public abstract class BaseModel implements IModel {

    protected final String TAG = this.getClass().getSimpleName();

    protected final Context mContext;

    protected Repository repository;

    public BaseModel(Context context) {
        mContext = context;
        repository = RepositoryImpl.INSTANCE;
    }

    @Override
    public void onDestroy() {
    }
}
