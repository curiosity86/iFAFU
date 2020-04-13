package cn.ifafu.ifafu.experiment.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.ifafu.ifafu.databinding.FragmentFeedbackBinding
import cn.ifafu.ifafu.ui.getViewModelFactory
import kotlinx.android.synthetic.main.fragment_feedback.view.*

class FeedbackFragment : Fragment() {

    private val viewModel: FeedbackViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFeedbackBinding.inflate(inflater, container, false).apply {
            vm = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //初始化返回按钮
        view.tb_score_filter.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

}