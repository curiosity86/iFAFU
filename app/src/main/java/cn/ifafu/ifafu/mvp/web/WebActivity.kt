package cn.ifafu.ifafu.mvp.web

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : BaseActivity<WebContract.Presenter>(), WebContract.View {

    private lateinit var progressDialog: ProgressDialog

    private lateinit var webView: WebView

    override fun initLayout(savedInstanceState: Bundle?): Int {
        return R.layout.activity_web
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_web)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()

        tb_web.setOnClickListener { finish() }

        mPresenter = WebPresenter(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setText("加载中")

        initWebView()

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView = WebView(applicationContext)
        val rootLayout = (findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup)
        rootLayout.addView(webView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT))
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideLoading()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showLoading()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                Log.d(TAG, request?.url.toString())
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        val webSettings = webView.settings
        webSettings.useWideViewPort = true
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.loadsImagesAutomatically = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        val cachePath = applicationContext.getDir("cache", Context.MODE_PRIVATE).path
        webSettings.setAppCachePath(cachePath)

        btn_refresh.setOnClickListener { webView.reload() }
    }

    override fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    override fun setTitle(title: String?, resId: Int?) {
        when {
            title != null -> tb_web.title = title
            resId != null -> tb_web.title = getString(resId)
        }
    }

    override fun showLoading() {
        if (!this.isDestroyed) {
            progressDialog.show()
        }
    }

    override fun hideLoading() {
        if (!this.isDestroyed) {
            progressDialog.cancel()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            //按返回键操作并且能回退网页
            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                //后退
                webView.goBack()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
