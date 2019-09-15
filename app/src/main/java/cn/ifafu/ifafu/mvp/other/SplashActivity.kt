package cn.ifafu.ifafu.mvp.other

import android.R.anim
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager.LayoutParams
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.local.RepositoryImpl
import cn.ifafu.ifafu.mvp.base.BaseActivity
import cn.ifafu.ifafu.mvp.base.i.IPresenter
import cn.ifafu.ifafu.mvp.exam.ExamActivity
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.main.MainActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import cn.ifafu.ifafu.util.AppUtils
import cn.ifafu.ifafu.util.RxUtils
import com.tencent.bugly.crashreport.CrashReport.CrashHandleCallback
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity<IPresenter>() {
    override fun initLayout(savedInstanceState: Bundle?): Int {
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //隐藏顶部状态栏
        window.addFlags(LayoutParams.FLAG_FULLSCREEN)
        return R.layout.activity_splash
    }

    @SuppressLint("CheckResult")
    override fun initData(savedInstanceState: Bundle?) {
        AppUtils.getMetaValue(context, "APP_ICON_HD")?.run {
            iv_app_icon.setImageDrawable(getDrawable(this as Int))
        }
        tv_app_name.text = AppUtils.getAppName(context)
        Observable
                .fromCallable {
                    val repository = RepositoryImpl.getInstance()
                    IFAFU.initConfig(context)
                    when (intent.getIntExtra("jump", -1)) {
                        Constant.ACTIVITY_SYLLABUS -> SyllabusActivity::class.java
                        Constant.ACTIVITY_EXAM -> ExamActivity::class.java
                        else -> {
                            var user: User? = repository.loginUser
                            if (user == null) {
                                user = repository.allUser.getOrNull(0)
                                if (user != null) {
                                    repository.saveLoginUser(user)
                                    MainActivity::class.java
                                } else {
                                    LoginActivity::class.java
                                }
                            } else {
                                MainActivity::class.java
                            }
                        }
                    }
                }
                .compose(RxUtils.ioToMain())
                .doFinally {
                    overridePendingTransition(anim.fade_in, anim.fade_out)
                    finish()
                }
                .subscribe({ clazz ->
                    val intent = Intent(this, clazz)
                    intent.putExtra("from", Constant.ACTIVITY_SCORE)
                    startActivity(intent)
                }, { throwable: Throwable ->
                    throwable.printStackTrace()
                    startActivity(Intent(this, LoginActivity::class.java))
                })
    }

    internal class MyCrashHandleCallback : CrashHandleCallback() {
        override fun onCrashHandleStart(crashType: Int,
                                        errorType: String?,
                                        errorMessage: String?,
                                        errorStack: String?
        ): MutableMap<String, String> {
            return mutableMapOf("account" to RepositoryImpl.getInstance().loginUser.account)
        }
    }
}