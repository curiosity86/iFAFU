package cn.ifafu.ifafu.ui.activity

import android.R.anim
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.ui.exam_list.ExamListActivity
import cn.ifafu.ifafu.ui.login.LoginActivity
import cn.ifafu.ifafu.ui.main.MainActivity
import cn.ifafu.ifafu.ui.syllabus.SyllabusActivity
import kotlinx.coroutines.Dispatchers
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
        GlobalScope.launch(Dispatchers.IO) {
            IFAFU.initConfig(applicationContext)
            val jumpActivityClass = when (intent.getIntExtra("jump", -1)) {
                Constant.ACTIVITY_SYLLABUS -> SyllabusActivity::class.java
                Constant.ACTIVITY_EXAM -> ExamListActivity::class.java
                else -> {
                    if (RepositoryImpl.user.getInUse() == null) {
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