package cn.ifafu.ifafu.mvp.activity

import android.R.anim
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager.LayoutParams
import cn.ifafu.ifafu.BuildConfig
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.databinding.SplashActivityBinding
import cn.ifafu.ifafu.entity.User
import cn.ifafu.ifafu.mvp.exam_list.ExamActivity
import cn.ifafu.ifafu.mvp.login.LoginActivity
import cn.ifafu.ifafu.mvp.main.MainActivity
import cn.ifafu.ifafu.mvp.syllabus.SyllabusActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<SplashActivityBinding>() {
    override fun getLayoutId(): Int {
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //隐藏顶部状态栏
        window.addFlags(LayoutParams.FLAG_FULLSCREEN)
        return R.layout.splash_activity
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        GlobalScope.launch(Dispatchers.IO) {
            mBinding.debug = BuildConfig.DEBUG
            IFAFU.initConfig(applicationContext)
            val repository = Repository
            val jumpActivityClass = when (intent.getIntExtra("jump", -1)) {
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
            val intent = Intent(this@SplashActivity, jumpActivityClass)
            intent.putExtra("from", Constant.ACTIVITY_SPLASH)
            startActivity(intent)
            overridePendingTransition(anim.fade_in, anim.fade_out)
            finish()
        }
    }

}