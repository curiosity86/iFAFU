package cn.ifafu.ifafu.app

import android.content.Context
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.di.appModule
import cn.ifafu.ifafu.util.AppUtils
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class IFAFU : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@IFAFU)
            modules(appModule)
        }
        UMConfigure.init(this, "5d4082673fc1955041000408", "web", UMConfigure.DEVICE_TYPE_PHONE, "1a446c1ae0455153aa502937a87e5634")
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        //后台自动登录
        loginJob = GlobalScope.launch {
            val user = Repository.user.getInUse() ?: return@launch
            kotlin.runCatching {
                Repository.user.save(Repository.user.login2(user.account, user.password).getOrNull() ?: return@launch)
            }
        }
    }

    companion object {

        var loginJob: Job? = null

        var FIRST_START_APP = true

        /**
         * 启动界面时调用，防止长时间白屏
         */
        fun initConfig(context: Context) {
            if (FIRST_START_APP) {
                initBugly(context)
                Repository.user.getInUseAccount().run {
                    if (this.isNotEmpty()) {
                        Bugly.setUserId(context, this)
                    }
                }
                Beta.enableHotfix = true
                FIRST_START_APP = false
            }
        }

        private fun initBugly(context: Context) {
            val strategy = CrashReport.UserStrategy(context)
            strategy.setCrashHandleCallback(object : CrashReport.CrashHandleCallback() {
                override fun onCrashHandleStart(crashType: Int, errorType: String?, errorMessage: String?, errorStack: String?): MutableMap<String, String> {
                    val map = super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack)
                            ?: HashMap()
                    map["account"] = Repository.user.getInUseAccount()
                    return map
                }
            })
            strategy.appVersion = AppUtils.getVersionName(context) + "-" + AppUtils.getVersionCode(context)
            Bugly.init(context, "46836c4eaa", BuildConfig.DEBUG, strategy)
        }
    }

}
