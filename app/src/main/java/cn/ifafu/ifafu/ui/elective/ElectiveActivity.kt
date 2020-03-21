package cn.ifafu.ifafu.ui.elective

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityElectiveBinding
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_elective.*

class ElectiveActivity : BaseActivity<ActivityElectiveBinding, ElectiveViewModel>() {

    override fun getLayoutId(): Int {
        return R.layout.activity_elective
    }

    override fun getViewModel(): ElectiveViewModel {
       return VMProvider(this)[ElectiveViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(mBinding.tbElective)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        mBinding.vm = mViewModel
        mBinding.btnFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackActivity::class.java))
        }
    }


}