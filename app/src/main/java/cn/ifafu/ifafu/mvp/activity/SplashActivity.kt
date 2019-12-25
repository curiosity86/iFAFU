package cn.ifafu.ifafu.mvp.activity

import android.R.anim
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager.LayoutParams
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.data.RepositoryImpl
import cn.ifafu.ifafu.entity.User
import cn.ifafu.ifafu.mvp.exam_list.ExamActivity
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.main.MainActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import cn.ifafu.ifafu.util.AppUtils
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.splash_activity.*

class SplashActivity : BaseActivity<IPresenter>() {
    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //隐藏顶部状态栏
        window.addFlags(LayoutParams.FLAG_FULLSCREEN)
        return R.layout.splash_activity
    }

    @SuppressLint("CheckResult")
    override fun initData(savedInstanceState: Bundle?) {
        AppUtils.getMetaValue(context, "APP_ICON_HD")?.run {
            iv_app_icon.setImageDrawable(getDrawable(this as Int))
        }
        tv_app_name.text = AppUtils.getAppName(context)
        Observable
                .fromCallable {
                    IFAFU.initConfig(applicationContext)
                    val repository = RepositoryImpl
                    when (intent.getIntExtra("jump", -1)) {
                        Constant.ACTIVITY_SYLLABUS -> SyllabusActivity::class.java
                        Constant.ACTIVITY_EXAM -> ExamActivity::class.java
                        else -> {
                            var user: User? = repository.getInUseUser()
                            if (user == null) {
                                user = repository.getAllUsers().getOrNull(0)
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
                    intent.putExtra("from", Constant.ACTIVITY_SPLASH)
                    startActivity(intent)
                }, { throwable: Throwable ->
                    throwable.printStackTrace()
                    startActivity(Intent(this, LoginActivity::class.java))
                })
    }

}