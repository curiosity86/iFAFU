package cn.ifafu.ifafu.app

import android.content.Context
import android.content.Intent
import android.util.Log
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.base.BaseApplication
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.login.LoginModel
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
            Log.e("RxJavaError", throwable.message ?: "RxJavaEmptyMessageError")
        }
        UMConfigure.init(this, "5d4082673fc1955041000408", "web", UMConfigure.DEVICE_TYPE_PHONE, "1a446c1ae0455153aa502937a87e5634")
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
////        UMConfigure.setLogEnabled(BuildConfig.DEBUG)
//        val mPushAgent = PushAgent.getInstance(this)
//        mPushAgent.register(object : IUmengRegisterCallback {
//            override fun onSuccess(p0: String?) {
//                Log.i("UMLog", "注册成功：deviceToken：-------->  $p0")
//            }
//
//            override fun onFailure(p0: String?, p1: String?) {
//                Log.i("UMLog", "注册失败：deviceToken：-------->  $p0, $p1")
//            }
//        })
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

                loginDisposable = LoginModel(context).reLogin()
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
