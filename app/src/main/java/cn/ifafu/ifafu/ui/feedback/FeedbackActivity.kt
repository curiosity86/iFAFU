package cn.ifafu.ifafu.ui.feedback

import android.os.Bundle
import androidx.activity.viewModels
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.databinding.ActivityFeedbackBinding
import cn.woolsen.easymvvm.base.BaseActivity

class FeedbackActivity : BaseActivity() {

    private val viewModel: FeedbackViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        with(bind<ActivityFeedbackBinding>(R.layout.activity_feedback)) {
            vm = viewModel
        }
    }
}