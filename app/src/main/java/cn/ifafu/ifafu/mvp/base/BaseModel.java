package cn.ifafu.ifafu.mvp.base;

import android.content.Context;

import cn.ifafu.ifafu.mvp.base.i.IModel;

public abstract class BaseModel implements IModel {

    protected final String TAG = this.getClass().getSimpleName();

    protected Context mContext;

    public BaseModel(Context context) {
        mContext = context;
    }

    @Override
    public void onDestroy() {

    }
}
