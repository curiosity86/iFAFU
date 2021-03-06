package cn.ifafu.ifafu.ui.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.webkit.*
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityWebBinding
import cn.ifafu.ifafu.ui.view.LoadingDialog
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : BaseActivity() {

    private val mLoadingDialog = LoadingDialog(this, "加载中")

    private lateinit var webView: WebView

    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private val PHOTO_REQUEST_CODE = 1023

    private var first = true

    private val mViewModel: WebViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        bind<ActivityWebBinding>(R.layout.activity_web)
        initWebView()
        mViewModel.loadUrl.observe(this, Observer {
            webView.loadUrl(it)
        })
        mViewModel.title.observe(this, Observer {
            tb_web.title = it
        })
        mViewModel.init(intent)
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
                mLoadingDialog.cancel()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                mLoadingDialog.show()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (first && intent.hasExtra("referer")) {
                    val referer = intent.getStringExtra("referer")
                    request?.requestHeaders?.put("referer", referer)
                    first = false
                }
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


    override fun onDestroy() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.cancel()
        }
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
