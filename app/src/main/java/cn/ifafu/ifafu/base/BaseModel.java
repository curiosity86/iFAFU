package cn.ifafu.ifafu.base;

import android.content.Context;

import cn.ifafu.ifafu.data.Repository;
import cn.ifafu.ifafu.data.local.LocalDataSource;
import cn.ifafu.ifafu.base.i.IModel;

public abstract class BaseModel implements IModel {

    protected final String TAG = this.getClass().getSimpleName();

    protected final Context mContext;

    protected LocalDataSource repository;

    public BaseModel(Context context) {
        mContext = context;
        repository = Repository.INSTANCE;
    }

    @Override
    public void onDestroy() {
    }
}
