package cn.ifafu.ifafu.app;

import android.util.Log;

import cn.ifafu.ifafu.mvp.base.BaseApplication;
import io.reactivex.plugins.RxJavaPlugins;

public class IFAFU extends BaseApplication {

    public static boolean FIRST_START_APP = true;

    @Override
    public void onCreate() {
        super.onCreate();
        RxJavaPlugins.setErrorHandler(throwable -> {
            Log.e("RxJavaError", throwable.getMessage() == null ? "RxJavaError" : throwable.getMessage());
        });
    }
}
