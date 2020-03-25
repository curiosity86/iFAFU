package cn.ifafu.ifafu.ui.elective

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityElectiveBinding
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog

class ElectiveActivity : BaseActivity() {

    private val loadingDialog = LoadingDialog(this)

    private val viewModel by viewModels<ElectiveViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        with(bind<ActivityElectiveBinding>(R.layout.activity_elective)) {
            vm = viewModel
            btnFeedback.setOnClickListener {
                startActivity(Intent(this@ElectiveActivity, FeedbackActivity::class.java))
            }
        }
        viewModel.loading.observe(this, Observer {
            if (it == null) {
                loadingDialog.cancel()
            } else {
                loadingDialog.show(it)
            }
        })
    }

}