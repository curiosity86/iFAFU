package cn.ifafu.ifafu.mvp.other

import android.R.anim
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager.LayoutParams
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.R.layout
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.exception.LoginInfoErrorException
import cn.ifafu.ifafu.data.local.RepositoryImpl
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.mvp.base.i.IPresenter
import cn.ifafu.ifafu.mvp.exam.ExamActivity
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.main.MainActivity
import cn.ifafu.ifafu.mvp.main.MainModel
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import cn.ifafu.ifafu.util.RxUtils
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.CrashHandleCallback
import io.reactivex.Observable

class SplashActivity : BaseActivity<IPresenter>() {
    override fun initLayout(savedInstanceState: Bundle?): Int {
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //隐藏顶部状态栏
        window.addFlags(LayoutParams.FLAG_FULLSCREEN)
        return layout.activity_splash
    }

    @SuppressLint("CheckResult")
    override fun initData(savedInstanceState: Bundle?) {
        Log.d("SplashActivity", "IFAFU.FIRST_START_APP: " + IFAFU.FIRST_START_APP)
        if (IFAFU.FIRST_START_APP) {
            IFAFU.loginDisposable = MainModel(this).reLogin()
                    .compose(RxUtils.ioToMain())
                    .subscribe({ }, { throwable: Throwable ->
                        Log.d("SplashActivity", "onError")
                        if (throwable is LoginInfoErrorException) {
                            startActivity(Intent(this, LoginActivity::class.java))
                            showMessage(throwable.message)
                        }
                        throwable.printStackTrace()
                    })
        }
        Observable
                .fromCallable {
                    initConfig()
                    when (intent.getIntExtra("jump", -1)) {
                        Constant.SYLLABUS_ACTIVITY -> return@fromCallable SyllabusActivity::class.java
                        Constant.EXAM_ACTIVITY -> return@fromCallable ExamActivity::class.java
                    }
                    val user: User? = RepositoryImpl.getInstance().user
                    if (user == null) {
                        LoginActivity::class.java
                    } else {
                        MainActivity::class.java
                    }
                }
                .compose(RxUtils.ioToMain())
                .doFinally {
                    overridePendingTransition(anim.fade_in, anim.fade_out)
                    finish()
                }
                .subscribe({ clazz ->
                    val intent = Intent(this, clazz)
                    intent.putExtra("from", Constant.SCORE_ACTIVITY)
                    startActivity(intent)
                }, { throwable: Throwable ->
                    throwable.printStackTrace()
                    startActivity(Intent(this, LoginActivity::class.java))
                })
    }

    private fun initConfig() {
        if (IFAFU.FIRST_START_APP) {
            val strategy = CrashReport.UserStrategy(applicationContext)
            strategy.setCrashHandleCallback(MyCrashHandleCallback())
            Bugly.init(applicationContext, "46836c4eaa", BuildConfig.DEBUG, strategy)
            Beta.enableHotfix = false
            IFAFU.FIRST_START_APP = false
        }
    }

    internal class MyCrashHandleCallback : CrashHandleCallback() {
        override fun onCrashHandleStart(crashType: Int,
                                        errorType: String?,
                                        errorMessage: String?,
                                        errorStack: String?
        ): MutableMap<String, String> {
            return mutableMapOf("account" to RepositoryImpl.getInstance().user.account)
        }
    }
}