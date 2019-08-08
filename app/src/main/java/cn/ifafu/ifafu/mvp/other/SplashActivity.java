package cn.ifafu.ifafu.mvp.other;

import android.content.Intent;
import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;
import com.tencent.bugly.Bugly;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.IFAFU;
import cn.ifafu.ifafu.data.entity.User;
import cn.ifafu.ifafu.mvp.login.LoginActivity;
import cn.ifafu.ifafu.mvp.main.MainActivity;
import cn.ifafu.ifafu.mvp.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparent(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bugly.init(getContext(), "46836c4eaa", false);
        User user = IFAFU.getUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
