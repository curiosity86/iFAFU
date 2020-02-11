package cn.ifafu.ifafu.ui.web

import android.app.Application
import android.content.Intent
import android.webkit.CookieManager
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.data.entity.ZFApiList
import cn.ifafu.ifafu.util.SPUtils

class WebViewModel(application: Application) : BaseViewModel(application) {

    val loadUrl by lazy { MutableLiveData<String>() }
    val title by lazy { MutableLiveData<String>() }

    fun init(intent: Intent) {
        safeLaunch {
            val title: String? = intent.getStringExtra("title")
            var url: String? = intent.getStringExtra("url")
            if (url == null) {
                this@WebViewModel.title.postValue("正方教务管理系统")
                url = School.getUrl(ZFApiList.MAIN, Repository.user.getInUse()!!)
                setCookie(url, SPUtils[Constant.SP_COOKIE].getString("ASP.NET_SessionId"))
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