package cn.ifafu.ifafu.experiment.ui.score.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.databinding.FragmentScoreFilterBinding
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.ui.view.adapter.ScoreFilterAdapter
import cn.ifafu.ifafu.ui.view.custom.RecyclerViewDivider
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_score_filter.view.*

// （已解决） 暂时不适用Navigation + Fragment
// 原因：对于ScoreFilter页面结束后，通知ScoreList刷新成绩无较好的Fragment解决方案
// （弃用）目前方案：通过Activity#onActivityResult
// 解决方案：通过Room+LiveData组件实现数据与数据库同步
class ScoreFilterFragment : Fragment(), Toolbar.OnMenuItemClickListener {

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
        mViewModel.init(args.year, args.term)
        mViewModel.scores.observe(viewLifecycleOwner, Observer {
            mAdapter.data = it
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

    interface OnRefreshListener {
        fun onRefresh()
    }

}
