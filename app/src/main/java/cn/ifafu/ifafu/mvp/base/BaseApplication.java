package cn.ifafu.ifafu.mvp.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

@SuppressLint("Registered")
public class BaseApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

}
