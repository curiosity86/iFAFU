package cn.ifafu.ifafu.mvp.other;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.data.local.DaoManager;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.mvp.exam.ExamActivity;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.mvp.main.MainActivity;
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity;
import cn.ifafu.ifafu.util.RxUtils;
import cn.ifafu.ifafu.util.SPUtils;
import io.reactivex.Observable;

public class SplashActivity extends BaseActivity {

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏顶部状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @SuppressLint("CheckResult")
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Observable
                .fromCallable(() -> {
                    if (IFAFU.FIRST_START_APP) {
                        Bugly.init(getApplicationContext(), "46836c4eaa", false);
                        Beta.enableHotfix = false;
                        IFAFU.FIRST_START_APP = false;
                    }
                    int jumpFlag = getIntent().getIntExtra("jump", -1);
                    switch (jumpFlag) {
                        case Constant.SYLLABUS_ACTIVITY:
                            return SyllabusActivity.class;
                        case Constant.EXAM_ACTIVITY:
                            return ExamActivity.class;
                    }
                    String account = SPUtils.get(Constant.SP_USER_INFO).getString("account");
                    User user = DaoManager.getInstance().getDaoSession().getUserDao().load(account);
                    if (user == null) {
                        return LoginActivity.class;
                    } else {
                        return MainActivity.class;
                    }
                })
                .compose(RxUtils.ioToMain())
                .doFinally(() -> {
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                })
                .subscribe(clazz -> {
                    Intent intent = new Intent(this, clazz);
                    intent.putExtra("from", Constant.SCORE_ACTIVITY);
                    startActivity(intent);
                    finish();
                }, throwable -> {
                    throwable.printStackTrace();
                    startActivity(new Intent(this, LoginActivity.class));
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
