package cn.ifafu.ifafu.mvp.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toolbar;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.ifafu.ifafu.R;
import cn.woolsen.android.mvp.BaseActivity;

public class WebActivity extends BaseActivity {

    private WebView webView;

    private String loadUrl;

    private Boolean js;

    private String referer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        if (referer != null) {
            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Referer", referer);
            webView.loadUrl(loadUrl, extraHeaders);
        } else {
            webView.loadUrl(loadUrl);
        }
        getStartUpParams();
        setWebViewSetting();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.setVisibility(View.GONE);
            long timeout = ViewConfiguration.getZoomControlsTimeout();
            webView.postDelayed(() -> webView.destroy(), timeout);
        }
        super.onDestroy();
    }

    private void setWebViewSetting() {
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(js);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(
                getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath());
    }

    private void getStartUpParams() {
        Bundle bundle = getIntent().getExtras();
        loadUrl = bundle.getString("loadUrl");

        Toolbar toolbar = findViewById(R.id.tb_web);
        toolbar.setTitle(bundle.getString("pageTitle"));
        toolbar.setNavigationOnClickListener(v -> back());

        js = bundle.getBoolean("js", true);

        if (bundle.getBoolean("cookie", false)) {
            CookieManager cookieManager = CookieManager.getInstance();

            CookieStore cookieStore = new java.net.CookieManager().getCookieStore();

            List<HttpCookie> cookies = cookieStore.getCookies();
            for (HttpCookie cookie : cookies) {
                cookieManager.setCookie(cookie.getDomain(), String.format(
                        Locale.getDefault(), "%s; domain=%s; path=%s",
                        cookie.toString(), cookie.getDomain(), cookie.getPath()));
            }
        }

        referer = bundle.getString("referer", null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            back();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void back() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            webView.destroy();
            finish();
        }
    }

}
