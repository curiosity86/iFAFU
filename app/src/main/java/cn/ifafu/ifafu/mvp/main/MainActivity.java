package cn.ifafu.ifafu.mvp.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.data.entity.Menu;
import cn.ifafu.ifafu.data.entity.Weather;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.mvp.other.AboutActivity;
import cn.ifafu.ifafu.util.ButtonUtils;
import cn.ifafu.ifafu.util.GlobalLib;
import cn.ifafu.ifafu.view.adapter.MenuAdapter;
import cn.ifafu.ifafu.view.listener.ZoomDrawerListener;

public class MainActivity extends BaseActivity<MainContract.Presenter>
        implements MainContract.View, View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private LinearLayout mContentLayout;
    private LinearLayout mLeftMenuView;
    private RecyclerView mMenuRecycleView;
    private MenuAdapter mMenuAdapter;

    private TextView weatherTV1;
    private TextView weatherTV2;

    private ImageView mLeftMenuIconIV;
    private TextView mLeftMenuNameTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalLib.transparentStatus(this);
        setContentView(R.layout.activity_main);

        mPresenter = new MainPresenter(this);

        initViewAndEvent();
        initNavigationView();

        mPresenter.onStart();
    }

    //初始化View
    private void initViewAndEvent() {
        weatherTV1 = findViewById(R.id.tv_weather_1);
        weatherTV2 = findViewById(R.id.tv_weather_2);
        mDrawerLayout = findViewById(R.id.drawer_main);
        mLeftMenuView = findViewById(R.id.left_menu_main);
        mContentLayout = findViewById(R.id.layout_content);
        mMenuRecycleView = findViewById(R.id.rv_menu);
        mLeftMenuIconIV = mLeftMenuView.findViewById(R.id.iv_menu_icon);
        mLeftMenuNameTV = mLeftMenuView.findViewById(R.id.tv_menu_name);
        findViewById(R.id.btn_menu).setOnClickListener(this);
    }

    //初始化侧滑栏样式
    private void initNavigationView() {
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.addDrawerListener(new ZoomDrawerListener(this, mContentLayout, mLeftMenuView));
        findViewById(R.id.tv_nav_about).setOnClickListener(this);
        findViewById(R.id.tv_nav_share).setOnClickListener(this);
        findViewById(R.id.tv_nav_fback).setOnClickListener(this);
        findViewById(R.id.tv_nav_update).setOnClickListener(this);
        findViewById(R.id.tv_nav_logout).setOnClickListener(this);
    }

    @Override
    public void setMenuAdapterData(List<Menu> menus) {
        if (mMenuAdapter == null) {
            mMenuAdapter = new MenuAdapter(this, menus);
            mMenuAdapter.setOnMenuClickListener((v, menu) -> {
                if (!ButtonUtils.isFastDoubleClick()) {
                    openActivity(new Intent(this, menu.getActivityClass()));
                }
            });
            mMenuRecycleView.setLayoutManager(new GridLayoutManager(
                    this, 4, RecyclerView.VERTICAL, false));
            mMenuRecycleView.setAdapter(mMenuAdapter);
        } else {
            mMenuAdapter.setMenuList(menus);
        }
    }

    @Override
    public void setLeftMenuHeadIcon(Drawable headIcon) {
        Glide.with(this)
                .load(headIcon)
                .into(mLeftMenuIconIV);
    }

    @Override
    public void setLeftMenuHeadName(String name) {
        mLeftMenuNameTV.setText(name);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers();
            } else if (ButtonUtils.isFastDoubleClick()) {
                finish();
            } else {
                showMessage(R.string.back_again);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_menu:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.tv_nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.tv_nav_share:
                mPresenter.shareApp();
                break;
            case R.id.tv_nav_fback:
                showMessage("反馈问题");
                break;
            case R.id.tv_nav_update:
                showMessage("检查更新");
                break;
            case R.id.tv_nav_logout:
                mPresenter.quitAccount();
                break;
        }
    }

    @Override
    public void setWeatherText(Weather weather) {
        weatherTV1.setText((weather.getNowTemp() + "℃"));
        weatherTV2.setText(String.format("%s | %s", weather.getCityName(), weather.getWeather()));
    }
}
