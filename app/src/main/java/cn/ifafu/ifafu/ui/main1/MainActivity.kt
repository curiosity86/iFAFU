package cn.ifafu.ifafu.ui.main1

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.util.ButtonUtils
import com.gyf.immersionbar.ImmersionBar

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).init()
        setContentView(R.layout.activity_main1)
    }

}