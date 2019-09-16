package cn.ifafu.ifafu.app

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException
import cn.ifafu.ifafu.mvp.base.BaseApplication
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.main.MainModel
import cn.ifafu.ifafu.mvp.other.SplashActivity
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
            Log.e("RxJavaError",
                    if (throwable.message == null) "RxJavaNullMessageError"
                    else throwable.message
            )
        }
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null)
        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
    }

    companion object {

        var FIRST_START_APP = true

        var loginDisposable: Disposable? = null

        fun initConfig(context: Context) {
            Log.d("IFAFU", "IFAFU.FIRST_START_APP = $FIRST_START_APP")
            if (FIRST_START_APP) {
                val strategy = CrashReport.UserStrategy(context)
                strategy.setCrashHandleCallback(SplashActivity.MyCrashHandleCallback())
                strategy.appVersion = AppUtils.getVersionName(context) + "-" + AppUtils.getVersionCode(context)
                println("Bugly APP_VERSION: " + strategy.appVersion)
                Bugly.init(context, "46836c4eaa", BuildConfig.DEBUG, strategy)
                Beta.enableHotfix = false
                FIRST_START_APP = false

                loginDisposable = MainModel(context).reLogin()
                        .compose(RxUtils.ioToMain())
                        .subscribe({ }, { throwable: Throwable ->
                            if (throwable is LoginInfoErrorException) {
                                context.startActivity(Intent(context, LoginActivity::class.java))
                            }
                            throwable.printStackTrace()
                        })
            }
        }

    }
}
