package cn.ifafu.ifafu.view.listener;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * 侧滑拖动监听器，用于拖动动画
 * Created by woolsen
 */
public class ZoomDrawerListener implements DrawerLayout.DrawerListener {

    private View mContentView;
    private View mLeftView;

    public ZoomDrawerListener(View contentView, View leftMenu) {
        mContentView = contentView;
        mLeftView = leftMenu;
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        mContentView.layout(mLeftView.getRight(), 0,
                mContentView.getMeasuredWidth() + mLeftView.getRight(),
                mContentView.getMeasuredHeight());
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
