package cn.ifafu.ifafu.app

import android.content.Context
import android.util.Log
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.util.AppUtils
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IFAFU : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { throwable ->
            Log.e("RxJavaError", throwable.message ?: "RxJavaEmptyMessageError")
        }
        UMConfigure.init(this, "5d4082673fc1955041000408", "web", UMConfigure.DEVICE_TYPE_PHONE, "1a446c1ae0455153aa502937a87e5634")
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

        loginJob = GlobalScope.launch(Dispatchers.IO) {
            val user = Repository.getInUseUser() ?: return@launch
            kotlin.runCatching {
                val response = Repository.login(user.account, user.password)
                if (user.name.isEmpty()) {
                    user.name = response.body!!
                    Repository.saveUser(user)
                }
            }
        }
    }

    companion object {

        var loginJob: Job? = null

        var FIRST_START_APP = true

        var loginDisposable: Disposable? = null

        fun initConfig(context: Context) {
            if (FIRST_START_APP) {
                //初始化Bugly
                val strategy = CrashReport.UserStrategy(context)
                strategy.setCrashHandleCallback(object : CrashReport.CrashHandleCallback() {
                    override fun onCrashHandleStart(crashType: Int, errorType: String?, errorMessage: String?, errorStack: String?): MutableMap<String, String> {
                        val map = super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack) ?: HashMap()
                        map["account"] = Repository.account
                        return map
                    }
                })
                strategy.appVersion = AppUtils.getVersionName(context) + "-" + AppUtils.getVersionCode(context)
                Bugly.init(context, "46836c4eaa", BuildConfig.DEBUG, strategy)
                Repository.account.run {
                    if (this.isNotEmpty()) {
                        Bugly.setUserId(context, this)
                    }
                }
                Beta.enableHotfix = true
                FIRST_START_APP = false
            }
        }

    }

}
