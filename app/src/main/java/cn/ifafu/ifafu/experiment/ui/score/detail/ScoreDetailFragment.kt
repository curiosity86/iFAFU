package cn.ifafu.ifafu.experiment.ui.score.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.databinding.FragmentScoreDetailBinding
import cn.ifafu.ifafu.ui.view.adapter.ScoreItemAdapter
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_score_detail.*
import kotlinx.android.synthetic.main.fragment_score_detail.view.tb_score_detail

class ScoreDetailFragment : Fragment() {

    private val args: ScoreDetailFragmentArgs by navArgs()

    private val mViewModel: ScoreDetailViewModel by viewModels { getViewModelFactory() }

    private val mAdapter by lazy { ScoreItemAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentScoreDetailBinding.inflate(inflater, container, false).apply {
            adapter = mAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //沉浸状态栏
        ImmersionBar.with(this)
                .titleBar(tb_score_detail)
                .statusBarDarkFont(true)
                .init()

        //初始化监听事件
        view.tb_score_detail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //初始化ViewModel
        mViewModel.score.observe(viewLifecycleOwner, Observer {
            mAdapter.addData(it)
        })
        mViewModel.init(args.scoreId)
    }
}