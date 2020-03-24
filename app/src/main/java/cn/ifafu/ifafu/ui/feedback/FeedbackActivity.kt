package cn.ifafu.ifafu.ui.feedback

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.databinding.ActivityFeedbackBinding
import cn.woolsen.easymvvm.base.BaseActivity

class FeedbackActivity : BaseActivity() {

    private val viewModel by viewModels<FeedbackViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        bind<ActivityFeedbackBinding>(R.layout.activity_feedback).apply {
            vm = viewModel
        }
        viewModel.toastMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }
}