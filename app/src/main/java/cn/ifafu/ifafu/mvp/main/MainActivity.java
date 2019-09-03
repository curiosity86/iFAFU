package cn.ifafu.ifafu.mvp.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.app.Constant;
import cn.ifafu.ifafu.data.entity.Menu;
import cn.ifafu.ifafu.data.entity.Weather;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.mvp.other.AboutActivity;
import cn.ifafu.ifafu.util.ButtonUtils;
import cn.ifafu.ifafu.view.adapter.MenuAdapter;
import cn.ifafu.ifafu.view.custom.DragLayout;
import cn.ifafu.ifafu.view.timeline.TimeAxis;
import cn.ifafu.ifafu.view.timeline.TimeLine;

public class MainActivity extends BaseActivity<MainContract.Presenter>
        implements MainContract.View {

    @BindView(R.id.iv_menu_icon)
    ImageView ivMenuIcon;
    @BindView(R.id.tv_menu_name)
    TextView tvMenuName;
    @BindView(R.id.tv_weather_1)
    TextView tvWeather1;
    @BindView(R.id.tv_weather_2)
    TextView tvWeather2;
    @BindView(R.id.rv_menu)
    RecyclerView rvMenu;
    @BindView(R.id.drawer_main)
    DragLayout drawerMain;
    @BindView(R.id.tv_course_title)
    TextView tvCourseTitle;
    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_course_address)
    TextView tvCourseAddress;
    @BindView(R.id.tv_course_time)
    TextView tvCourseTime;
    @BindView(R.id.view_timeline)
    TimeLine timeLine;

    private MenuAdapter mMenuAdapter;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StatusBarUtil.setTransparent(this);
        ViewGroup contentView = getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
        contentView.getChildAt(0).setFitsSystemWindows(false);
        mPresenter = new MainPresenter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.updateView();
    }

    @Override
    public void setMenuAdapterData(List<Menu> menus) {
        if (mMenuAdapter == null) {
            mMenuAdapter = new MenuAdapter(this, menus);
            mMenuAdapter.setOnMenuClickListener((v, menu) -> {
                if (!ButtonUtils.isFastDoubleClick()) {
                    openActivity(menu.getIntent());
                }
            });
            rvMenu.setLayoutManager(new GridLayoutManager(
                    this, 4, RecyclerView.VERTICAL, false));
            rvMenu.setAdapter(mMenuAdapter);
        } else {
            mMenuAdapter.setMenuList(menus);
        }
    }

    @Override
    public void setLeftMenuHeadIcon(Drawable headIcon) {
        Glide.with(this)
                .load(headIcon)
                .into(ivMenuIcon);
    }

    @Override
    public void setLeftMenuHeadName(String name) {
        tvMenuName.setText(name);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (drawerMain.getStatus() == DragLayout.Status.Open) {
                drawerMain.close(true);
            } else if (ButtonUtils.isFastDoubleClick()) {
                finish();
            } else {
                showMessage(R.string.back_again);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.btn_menu, R.id.tv_nav_about, R.id.tv_nav_share, R.id.tv_nav_fback,
            R.id.tv_nav_update, R.id.tv_nav_logout})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_menu:
                drawerMain.open();
                break;
            case R.id.tv_nav_update:
                mPresenter.updateApp();
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
            case R.id.tv_nav_logout:
                mPresenter.quitAccount();
                break;
        }
    }

    @Override
    public void setWeatherText(Weather weather) {
        tvWeather1.setText((weather.getNowTemp() + "℃"));
        tvWeather2.setText(String.format("%s | %s", weather.getCityName(), weather.getWeather()));
    }

    @Override
    public void setCourseText(String title, String name, String address, String time) {
        Log.d(TAG, title + "   " + name + "   " + address + "    " + time);
        tvCourseTitle.setText(title);
        tvCourseName.setText(name);
        tvCourseAddress.setText(address);
        tvCourseTime.setText(time);
    }

    @Override
    public void setTimeLineData(List<TimeAxis> data) {
        timeLine.setTimeAxisList(data)
                .invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constant.SYLLABUS_ACTIVITY) {
            mPresenter.updateCourseView();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
