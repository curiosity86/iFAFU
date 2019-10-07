package cn.ifafu.ifafu.mvp.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import android.widget.LinearLayout
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : BaseActivity<WebContract.Presenter>(), WebContract.View {

    private lateinit var progressDialog: ProgressDialog

    private lateinit var webView: WebView

    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private val PHOTO_REQUEST_CODE = 1023

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
        webView = WebView(this)
        view_root.addView(webView, LinearLayout.LayoutParams(
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
                mFilePathCallback = filePathCallback
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PHOTO_REQUEST_CODE)
                return true
            }
        }
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
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
        if (requestCode == PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.run {
                mFilePathCallback?.onReceiveValue(arrayOf(this))
            }
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

    override fun onDestroy() {
        hideLoading()
        super.onDestroy()
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
