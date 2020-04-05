package cn.ifafu.ifafu.experiment.ui.elective

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.databinding.FragmentElectiveBinding
import cn.ifafu.ifafu.ui.feedback.FeedbackActivity
import cn.ifafu.ifafu.ui.view.LoadingDialog

class ElectiveFragment : Fragment() {

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    private val viewModel by viewModels<ElectiveViewModel> { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val listener: (View, Score) -> Unit = { view, score ->
            val action = ElectiveFragmentDirections
                    .actionFragmentElectiveToFragmentScoreDetail2(score.id)
            findNavController().navigate(action)
        }
        val binding = FragmentElectiveBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            btnFeedback.setOnClickListener {
                startActivity(Intent(requireContext(), FeedbackActivity::class.java))
            }
            eTotal.setOnScoreClickListener(listener)
            eCxcy.setOnScoreClickListener(listener)
            eRwsk.setOnScoreClickListener(listener)
            eWxsy.setOnScoreClickListener(listener)
            eYsty.setOnScoreClickListener(listener)
            eZrkx.setOnScoreClickListener(listener)
        }
        loadingDialog.observe(viewLifecycleOwner, viewModel.loading)
        return binding.root
    }

}