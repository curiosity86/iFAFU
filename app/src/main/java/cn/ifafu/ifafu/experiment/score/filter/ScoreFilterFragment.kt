package cn.ifafu.ifafu.experiment.score.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.databinding.FragmentScoreFilterBinding
import cn.ifafu.ifafu.view.adapter.ScoreFilterAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider

class ScoreFilterFragment : Fragment() {

    private val mAdapter by lazy {
        ScoreFilterAdapter(requireContext()) {
            mViewModel.itemChecked(it)
        }
    }

    private val args: ScoreFilterFragmentArgs by navArgs()
    private val mViewModel: ScoreFilterViewModel by viewModels { getViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentScoreFilterBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = mViewModel
            adapter = mAdapter
            rvScoreFilter.addItemDecoration(RecyclerViewDivider(
                    requireContext(), LinearLayoutManager.VERTICAL, R.drawable.shape_divider))
            tbScoreFilter.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            btnFilterAll.setOnClickListener {
                mAdapter.setAllChecked()
                mViewModel.allChecked()
            }
        }
        mViewModel.init(args.year, args.term)
        mViewModel.scores.observe(viewLifecycleOwner, Observer {
            mAdapter.data = it
        })
        return binding.root
    }

}
