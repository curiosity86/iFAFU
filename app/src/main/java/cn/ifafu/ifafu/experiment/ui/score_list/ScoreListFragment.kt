package cn.ifafu.ifafu.experiment.ui.score_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseSimpleFragment
import cn.ifafu.ifafu.databinding.FragmentScoreListBinding
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.ui.view.SemesterPicker
import cn.ifafu.ifafu.ui.view.custom.EmptyView
import cn.ifafu.ifafu.ui.view.custom.RecyclerViewDivider
import com.afollestad.materialdialogs.MaterialDialog
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_score_list.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScoreListFragment : BaseSimpleFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private val mAdapter: ScoreListAdapter = ScoreListAdapter()

    private val iesDetailDialog by lazy {
        MaterialDialog(requireContext()).apply {
            title(text = "智育分计算详情")
            negativeButton(text = "智育分计算规则") {
                MaterialDialog(requireContext()).show {
                    title(text = "智育分计算规则")
                    message(res = R.string.score_ies_rule)
                    positiveButton(text = "收到")
                }
            }
            positiveButton(text = "好嘞~")
        }
    }

    private val mSemesterPicker by lazy {
        SemesterPicker(requireContext()) { year, term ->
            mViewModel.switchYearAndTerm(year, term)
        }
    }

    private val mLoadingDialog by lazy { LoadingDialog(requireContext()) }

    private val mViewModel by viewModel<ScoreListViewModel>()

    private lateinit var mBinding: FragmentScoreListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentScoreListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = mViewModel
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //沉浸状态栏
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .titleBar(view.tb_score_list)
                .init()

        //初始化监听事件
        view.tv_score_title.setOnClickListener(this@ScoreListFragment)
        view.layout_ies.setOnClickListener(this@ScoreListFragment)
        view.layout_cnt.setOnClickListener(this@ScoreListFragment)
        view.tb_score_list.setOnMenuItemClickListener(this)
        view.tb_score_list.setNavigationOnClickListener {
            requireActivity().finish()
        }

        //初始化RecycleView
        view.rv_score.addItemDecoration(RecyclerViewDivider(
                requireContext(), LinearLayoutManager.VERTICAL, R.drawable.shape_divider))
        mAdapter.setEmptyView(EmptyView(context).apply {
            setTitle("本学期暂无成绩信息")
        })
        view.rv_score.adapter = mAdapter

        //初始化ViewModel
        mViewModel.iesDetail.observe(viewLifecycleOwner, Observer {
            it.runContentIfNotHandled {
                iesDetailDialog.show { message(text = it) }
            }
        })
        mViewModel.semester.observe(viewLifecycleOwner, Observer { event ->
            event.runContentIfNotHandled { s ->
                mSemesterPicker.setSelections(s.yearList, s.termList)
                mSemesterPicker.setIndex(s.yearIndex, s.termIndex)
            }
        })
        mViewModel.scoresResource.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    val message = resource.message
                    if (message != null) {
                        toast(message)
                    }
                    mAdapter.setDiffNewData(resource.data.toMutableList())
                    mLoadingDialog.cancel()
                }
                is Resource.Error -> {
                    toast(resource.message)
                    mLoadingDialog.cancel()
                }
                is Resource.Loading -> {
                    mLoadingDialog.show()
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_score_title ->
                mSemesterPicker.show()
            R.id.layout_ies ->
                mViewModel.iesCalculationDetail()
            R.id.layout_cnt -> {
                val action = ScoreListFragmentDirections
                        .actionFragmentScoreListToFragmentScoreFilter(
                                mSemesterPicker.yearStr, mSemesterPicker.termStr)
                findNavController().navigate(action)
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                mViewModel.refreshScoreList()
            }
            R.id.menu_filter -> {
                val action = ScoreListFragmentDirections
                        .actionFragmentScoreListToFragmentScoreFilter(
                                mSemesterPicker.yearStr, mSemesterPicker.termStr)
                findNavController().navigate(action)
            }
        }
        return true
    }

}