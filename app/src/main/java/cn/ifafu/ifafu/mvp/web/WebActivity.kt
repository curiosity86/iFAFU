package cn.ifafu.ifafu.mvp.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.util.DensityUtils
import cn.ifafu.ifafu.util.Glide4Engine
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.gyf.immersionbar.ImmersionBar
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : BaseActivity<WebContract.Presenter>(), WebContract.View {

    private lateinit var progressDialog: ProgressDialog

    private lateinit var webView: WebView

    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private val REQUEST_CODE_CHOOSE_ACTIVITY = 1023

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
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

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                Log.d("WebChromeClient", "onShowFileChooser")
                mFilePathCallback = filePathCallback
                Matisse.from(this@WebActivity)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .maxSelectable(3)
                        .gridExpectedSize(DensityUtils.dp2px(context, 120F).toInt())
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(Glide4Engine())
                        .forResult(REQUEST_CODE_CHOOSE_ACTIVITY)
                return true
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CHOOSE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            mFilePathCallback?.onReceiveValue(Matisse.obtainResult(data).toTypedArray())
        }
        super.onActivityResult(requestCode, resultCode, data)
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
