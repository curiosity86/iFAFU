package cn.ifafu.ifafu.experiment.ui.score_filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.databinding.FragmentScoreFilterBinding
import cn.ifafu.ifafu.ui.view.custom.RecyclerViewDivider
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_score_filter.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ScoreFilterFragment : Fragment(), Toolbar.OnMenuItemClickListener {

    private val mAdapter by lazy {
        ScoreFilterAdapter {
            mViewModel.itemChecked(it)
        }
    }

    private val args: ScoreFilterFragmentArgs by navArgs()
    private val mViewModel by viewModel<ScoreFilterViewModel> { parametersOf(args.year, args.term) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentScoreFilterBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = mViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //沉浸状态栏
        ImmersionBar.with(this)
                .titleBar(view.tb_score_filter)
                .statusBarDarkFont(true)
                .init()

        //初始化监听事件
        view.tb_score_filter.setOnMenuItemClickListener(this)
        view.tb_score_filter.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //初始化RecycleView
        view.rv_score_filter.adapter = mAdapter
        view.rv_score_filter.addItemDecoration(RecyclerViewDivider(
                requireContext(), LinearLayoutManager.VERTICAL, R.drawable.shape_divider))

        //初始化ViewModel
        mViewModel.scores.observe(viewLifecycleOwner, Observer {
            mAdapter.setDiffNewData(it.toMutableList())
        })
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_filter_all -> {
                mAdapter.setAllChecked()
                mViewModel.allChecked()
            }
        }
        return true
    }

}
