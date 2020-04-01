package cn.ifafu.ifafu.experiment.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityFeedbackBinding
import cn.ifafu.ifafu.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {

    private val viewModel: FeedbackViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

}