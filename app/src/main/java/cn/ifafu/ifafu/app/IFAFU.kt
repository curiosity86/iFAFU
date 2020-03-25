package cn.ifafu.ifafu.app

import android.app.Application
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.util.AppUtils
import cn.ifafu.ifafu.util.SPUtils
import com.tencent.bugly.Bugly
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class IFAFU : BaseApplication() {

    companion object {

        var isInitConfig = false

        /**
         * 启动界面时调用，防止长时间白屏
         * 必须在主线程初始化！！！(已设置Dispatchers.Main)
         *
         * @param application Application
         */
        suspend fun initConfig(application: Application) = withContext(Dispatchers.Main) {
            if (!isInitConfig) {
                isInitConfig = true
                /* 初始化Timber */
                Timber.plant(Timber.DebugTree())
                /* 初始化Repository并于内部自动登录 */
                RepositoryImpl.init(application)
                /* 初始化友盟 */
                UMConfigure.init(application, "5d4082673fc1955041000408", "web", UMConfigure.DEVICE_TYPE_PHONE, "1a446c1ae0455153aa502937a87e5634")
                MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
                /* 初始化Bugly */
                Bugly.setUserId(application, SPUtils[Constant.SP_USER_INFO].getString("account"))
                val strategy = CrashReport.UserStrategy(application)
                strategy.setCrashHandleCallback(CrashCallback())
                strategy.appVersion = AppUtils.getVersionName(application) + "-" + AppUtils.getVersionCode(application)
                Bugly.init(application, "46836c4eaa", BuildConfig.DEBUG, strategy)
            }
        }
    }

    private class CrashCallback : CrashReport.CrashHandleCallback() {
        override fun onCrashHandleStart(crashType: Int,
                                        errorType: String?,
                                        errorMessage: String?,
                                        errorStack: String?): MutableMap<String, String> {
            return (super.onCrashHandleStart(crashType, errorType, errorMessage, errorStack)
                    ?: HashMap()).apply {
                put("account", SPUtils[Constant.SP_USER_INFO].getString("account"))
            }
        }
    }

}
