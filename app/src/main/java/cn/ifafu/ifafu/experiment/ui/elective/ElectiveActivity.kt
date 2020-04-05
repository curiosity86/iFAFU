package cn.ifafu.ifafu.experiment.ui.elective

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityElectiveBinding

class ElectiveActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        DataBindingUtil.setContentView<ActivityElectiveBinding>(this, R.layout.activity_elective)
    }

}