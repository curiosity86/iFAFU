package cn.ifafu.ifafu.experiment.score.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.databinding.FragmentScoreDetailBinding
import cn.ifafu.ifafu.view.adapter.ScoreItemAdapter

class ScoreDetailFragment : Fragment() {

    private val args: ScoreDetailFragmentArgs by navArgs()

    private val mViewModel: ScoreDetailViewModel by viewModels { getViewModelFactory() }

    private val mAdapter by lazy { ScoreItemAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentScoreDetailBinding.inflate(inflater, container, false).apply {
            adapter = mAdapter
            tbScoreItem.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        mViewModel.score.observe(viewLifecycleOwner, Observer {
            mAdapter.replaceData(it)
        })
        mViewModel.init(args.scoreId)
        return binding.root
    }

}