package cn.ifafu.ifafu.mvp.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.jaeger.library.StatusBarUtil;

import cn.ifafu.ifafu.R;
import cn.ifafu.ifafu.mvp.base.BaseActivity;
import cn.ifafu.ifafu.view.custom.WToolbar;
import cn.ifafu.ifafu.view.dialog.ProgressDialog;

public class WebActivity extends BaseActivity<WebContract.Presenter> implements WebContract.View {

    private ProgressDialog progressDialog;

    private WebView webView;

    private WToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setLightMode(this);

        mPresenter = new WebPresenter(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setText("加载中");

        webView = findViewById(R.id.view_web);

        initWebView();

        toolbar = findViewById(R.id.tb_web);
        toolbar.setOnClickListener(v -> finish());

        mPresenter.onStart();
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
        toolbar.setTitle(title);
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
}
