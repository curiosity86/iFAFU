package cn.ifafu.ifafu.ui.web

import android.app.Application
import android.content.Intent
import android.webkit.CookieManager
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.data.bean.ZFApiList
import cn.ifafu.ifafu.util.SPUtils

class WebViewModel(application: Application) : BaseViewModel(application) {

    val loadUrl by lazy { MutableLiveData<String>() }
    val title by lazy { MutableLiveData<String>() }

    fun init(intent: Intent) {
        safeLaunchWithMessage {
            val title: String? = intent.getStringExtra("title")
            var url: String? = intent.getStringExtra("url")
            if (url == null) {
                this@WebViewModel.title.postValue("正方教务管理系统")
                url = Constant.getUrl(ZFApiList.MAIN, RepositoryImpl.user.getInUse()!!)
                setCookie(url, SPUtils[Constant.SP_COOKIE].getString("cookie"))
            }
            if (title != null) {
                this@WebViewModel.title.postValue(title)
            }
            this@WebViewModel.loadUrl.postValue(url)
        }
    }

    private fun setCookie(url: String, cookie: String?) {
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.flush()
        cookieManager.setAcceptCookie(true)
        cookieManager.setCookie(url, cookie)
    }
}