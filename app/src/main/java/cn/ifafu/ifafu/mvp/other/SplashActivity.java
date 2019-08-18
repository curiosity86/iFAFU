package cn.ifafu.ifafu.mvp.other;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.tencent.bugly.Bugly;

import java.util.concurrent.Callable;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.mvp.main.MainActivity;
import cn.ifafu.ifafu.util.RxUtils;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;

public class SplashActivity extends BaseActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏顶部状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Observable
                .fromCallable((Callable<Class<? extends Activity>>) () -> {
                    Bugly.init(getApplicationContext(), "46836c4eaa", false);
                    String account = SPUtils.get(Constant.SP_USER_INFO).getString("account");
                    User user = DaoManager.getInstance().getDaoSession().getUserDao().load(account);
                    if (user == null) {
                        return LoginActivity.class;
                    } else {
                        return MainActivity.class;
                    }
                })
                .compose(RxUtils.ioToMain())
                .subscribe(clazz -> {
                    startActivity(new Intent(this, clazz));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }, throwable -> {
                    throwable.printStackTrace();
                    startActivity(new Intent(this, LoginActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                });
    }

}
