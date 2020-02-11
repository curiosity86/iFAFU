package cn.ifafu.ifafu.ui.activity

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
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.databinding.SplashActivityBinding
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.login.LoginActivity
import cn.ifafu.ifafu.ui.main.MainActivity
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity<SplashActivityBinding, BaseViewModel>() {

    override fun getViewModel(): BaseViewModel? = null

    override fun getLayoutId(): Int {
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //隐藏顶部状态栏
        window.addFlags(LayoutParams.FLAG_FULLSCREEN)
        return R.layout.splash_activity
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        GlobalScope.launch {
            IFAFU.initConfig(applicationContext)
            mBinding.debug = BuildConfig.DEBUG
            val jumpActivityClass = when (intent.getIntExtra("jump", -1)) {
                Constant.ACTIVITY_SYLLABUS -> SyllabusActivity::class.java
                Constant.ACTIVITY_EXAM -> ExamListActivity::class.java
                else -> {
                    if (Repository.user.getInUse() == null) {
                        LoginActivity::class.java
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