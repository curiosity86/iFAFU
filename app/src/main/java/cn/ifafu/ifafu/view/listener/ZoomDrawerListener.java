package cn.ifafu.ifafu.view.listener;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

public class ZoomDrawerListener implements DrawerLayout.DrawerListener {

    private Activity mActivity;
    private DrawerLayout mDrawerLayout;
    private View mContentView;
    private View mLeftView;

    public ZoomDrawerListener(Activity activity, DrawerLayout drawerLayout, View contentView, View leftMenu) {
        mActivity = activity;
        mDrawerLayout = drawerLayout;
        mContentView = contentView;
        mLeftView = leftMenu;
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mContentView.layout(mLeftView.getRight(), 0,
                displayMetrics.widthPixels + mLeftView.getRight(),
                displayMetrics.heightPixels);
        dragToZoomView(slideOffset);
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    /**
     * 缩放view
     *
     * @param percent 拖动百分比
     */
    private void dragToZoomView(float percent) {
        mContentView.setScaleX(1f - 0.3f * percent);
        mContentView.setScaleY(1f - 0.3f * percent);
        mContentView.setTranslationX(-0.1f * percent * mContentView.getWidth());
        mLeftView.setScaleX(percent * 0.5f + 0.5f);
        mLeftView.setScaleY(percent * 0.5f + 0.5f);
        mLeftView.setTranslationX(0.2f * (1 - percent) * mLeftView.getWidth());
    }

    private void l(String msg) {
        Log.d("ZoomDrawerListener", msg);
    }
}
