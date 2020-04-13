package cn.ifafu.ifafu.ui.activity

import android.R.anim
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import cn.ifafu.ifafu.IFAFU
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.constant.Constant
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.experiment.ui.login.LoginActivity
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.main.MainActivity
import cn.ifafu.ifafu.ui.schedule.SyllabusActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //隐藏顶部状态栏
        window.addFlags(LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch {
            IFAFU.initConfig(application)
            val activity: Class<out Activity>
            val jump = intent.getIntExtra("jump", -1)
            val from = intent.getIntExtra("from", -1)
            when {
                jump == Constant.ACTIVITY_SYLLABUS -> {
                    activity = SyllabusActivity::class.java
                }
                jump == Constant.ACTIVITY_EXAM -> {
                    activity = ExamListActivity::class.java
                }
                RepositoryImpl.user.getInUse() == null -> {
                    activity = LoginActivity::class.java
                }
                from == Constant.SYLLABUS_WIDGET -> {
                    activity = SyllabusActivity::class.java
                }
                else -> {
                    activity = MainActivity::class.java
                }
            }
            val intent = Intent(this@SplashActivity, activity)
            intent.putExtra("from", Constant.ACTIVITY_SPLASH)
            overridePendingTransition(anim.fade_in, anim.fade_out)
            startActivity(intent)
            finish()
        }
    }

}