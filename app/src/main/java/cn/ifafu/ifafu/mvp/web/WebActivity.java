package cn.ifafu.ifafu.mvp.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.view.custom.WToolbar;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;

public class WebActivity extends BaseActivity<WebContract.Presenter> implements WebContract.View {

    private ProgressDialog progressDialog;

    @BindView(R.id.view_web)
    WebView webView;
    @BindView(R.id.tb_web)
    WToolbar tbWeb;
    @BindView(R.id.btn_refresh)
    ImageButton btnRefresh;

    @Override
    public int initLayout(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_web;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tbWeb)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init();

        mPresenter = new WebPresenter(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setText("加载中");

        initWebView();

        tbWeb.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> webView.reload());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLoading();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoading();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d(TAG, request.getUrl().toString());
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(
                getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath());
    }

    @Override
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    @Override
    public void setTitle(String title) {
        tbWeb.setTitle(title);
    }

    @Override
    public void showLoading() {
        if (!this.isDestroyed()) {
            progressDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (!this.isDestroyed()) {
            progressDialog.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            //按返回键操作并且能回退网页
            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                //后退
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
