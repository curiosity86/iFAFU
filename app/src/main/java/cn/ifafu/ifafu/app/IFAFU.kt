package cn.ifafu.ifafu.app

import android.content.Intent
import android.util.Log
import android.widget.Toast
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException
import cn.ifafu.ifafu.mvp.base.BaseApplication
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.main.MainModel
import cn.ifafu.ifafu.mvp.other.SplashActivity
import cn.ifafu.ifafu.util.RxUtils
import cn.ifafu.ifafu.util.ToastUtils
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins

class IFAFU : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler { throwable -> Log.e("RxJavaError", if (throwable.message == null) "RxJavaError" else throwable.message) }
        Log.d("IFAFU", "IFAFU.FIRST_START_APP = $FIRST_START_APP")
        if (FIRST_START_APP) {
            loginDisposable = MainModel(this).reLogin()
                    .compose(RxUtils.ioToMain())
                    .subscribe({ }, { throwable: Throwable ->
                        if (throwable is LoginInfoErrorException) {
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        ToastUtils.showToast(applicationContext, throwable.message, Toast.LENGTH_SHORT)
                        throwable.printStackTrace()
                    })
            Thread { initConfig() }.start()
        }
    }

    private fun initConfig() {
        if (FIRST_START_APP) {
            val strategy = CrashReport.UserStrategy(applicationContext)
            strategy.setCrashHandleCallback(SplashActivity.MyCrashHandleCallback())
            Bugly.init(applicationContext, "46836c4eaa", BuildConfig.DEBUG, strategy)
            Beta.enableHotfix = false
            FIRST_START_APP = false
        }
    }

    companion object {

        var FIRST_START_APP = true

        var loginDisposable: Disposable? = null
    }
}
