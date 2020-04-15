package cn.ifafu.ifafu.experiment.ui.score_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cn.ifafu.ifafu.databinding.FragmentScoreDetailBinding
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_score_detail.*
import kotlinx.android.synthetic.main.fragment_score_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ScoreDetailFragment : Fragment() {

    private val args: ScoreDetailFragmentArgs by navArgs()

    private val mViewModel by viewModel<ScoreDetailViewModel> { parametersOf(args.scoreId) }

    private val mAdapter = ScoreDetailAdapter()

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
        mViewModel.scoreMap.observe(viewLifecycleOwner, Observer {
            mAdapter.addData(it)
        })
    }
}