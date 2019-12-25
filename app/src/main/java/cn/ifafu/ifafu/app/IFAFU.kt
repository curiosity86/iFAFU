package cn.ifafu.ifafu.app

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.RepositoryImpl
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.login.LoginModel
import cn.ifafu.ifafu.util.AppUtils
import cn.ifafu.ifafu.util.RxUtils
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins

class IFAFU : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { throwable ->
            Log.e("RxJavaError", throwable.message ?: "RxJavaEmptyMessageError")
        }
        UMConfigure.init(this, "5d4082673fc1955041000408", "web", UMConfigure.DEVICE_TYPE_PHONE, "1a446c1ae0455153aa502937a87e5634")
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        RepositoryImpl.init(this)
    }

    companion object {

        var FIRST_START_APP = true

        var loginDisposable: Disposable? = null

        fun initConfig(context: Context) {
            if (FIRST_START_APP) {
                //初始化Bugly
                val strategy = CrashReport.UserStrategy(context)
                strategy.setCrashHandleCallback(MyCrashHandleCallback())
                strategy.appVersion = AppUtils.getVersionName(context) + "-" + AppUtils.getVersionCode(context)
                Bugly.init(context, "46836c4eaa", BuildConfig.DEBUG, strategy)
                RepositoryImpl.account.run {
                    if (this.isNotEmpty()) {
                        Bugly.setUserId(context, this)
                    }
                }
                Beta.enableHotfix = true
                loginDisposable = LoginModel(context).reLogin()
                        .compose(RxUtils.ioToMain())
                        .subscribe({ }, { throwable: Throwable ->
                            if (throwable is LoginInfoErrorException) {
                                context.startActivity(Intent(context, LoginActivity::class.java))
                            }
                            throwable.printStackTrace()
                        })

                FIRST_START_APP = false
            }
        }

    }

    private class MyCrashHandleCallback : CrashReport.CrashHandleCallback() {
        override fun onCrashHandleStart(crashType: Int,
                                        errorType: String?,
                                        errorMessage: String?,
                                        errorStack: String?
        ): MutableMap<String, String> {
            return mutableMapOf("account" to RepositoryImpl.account)
        }
    }
}
