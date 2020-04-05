package cn.ifafu.ifafu.experiment.score.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseSimpleFragment
import cn.ifafu.ifafu.constant.ACTIVITY_SCORE_FILTER
import cn.ifafu.ifafu.databinding.FragmentScoreListBinding
import cn.ifafu.ifafu.experiment.score.filter.ScoreFilterActivity
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.ui.view.SemesterOptionPicker
import cn.ifafu.ifafu.ui.view.adapter.ScoreAdapter
import cn.ifafu.ifafu.ui.view.custom.RecyclerViewDivider
import com.afollestad.materialdialogs.MaterialDialog
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_score_list.view.*
import timber.log.Timber

class ScoreListFragment : BaseSimpleFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private val mAdapter: ScoreAdapter by lazy {
        ScoreAdapter(requireContext()).apply {
            setOnScoreClickListener { v, score ->
                val action = ScoreListFragmentDirections
                        .actionFragmentScoreListToFragmentScoreDetail(score.id)
                findNavController().navigate(action)
            }
        }
    }

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

    private val mSemesterOptionPicker by lazy {
        SemesterOptionPicker(requireContext()) { year, term ->
            mViewModel.switchYearAndTerm(year, term)
        }
    }

    private val mLoadingDialog by lazy { LoadingDialog(requireContext()) }

    private val mViewModel: ScoreListViewModel by viewModels { getViewModelFactory() }

    private var mBinding: FragmentScoreListBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentScoreListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = mViewModel
        }
        mBinding = binding
        return binding.root
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
            findNavController().popBackStack()
        }

        //初始化RecycleView
        view.rv_score.addItemDecoration(RecyclerViewDivider(
                requireContext(), LinearLayoutManager.VERTICAL, R.drawable.shape_divider))
        view.rv_score.adapter = mAdapter

        //初始化ViewModel
        mViewModel.iesDetail.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                iesDetailDialog.show { message(text = it) }
            }
        })
        mViewModel.scores.observe(viewLifecycleOwner, Observer {
            mAdapter.scoreList = it
            mAdapter.notifyDataSetChanged()
        })
        mLoadingDialog.observe(viewLifecycleOwner, mViewModel.loading)
    }

    private var first = false

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_score_title -> {
                mViewModel.semester.value?.run {
                    mSemesterOptionPicker.setSemester(this)
                    mSemesterOptionPicker.show()
                }
            }
            R.id.layout_ies -> mViewModel.iesCalculationDetail()
            R.id.layout_cnt -> {
                val semester = mViewModel.semester.value
                if (semester == null) {
                    toast("未找到学期信息")
                    return
                }
//                val action = ScoreListFragmentDirections
//                        .actionFragmentScoreListToFragmentScoreFilter(semester.yearStr, semester.termStr)
//                findNavController().navigate(action)
                val intent = Intent(requireContext(), ScoreFilterActivity::class.java).apply {
                    putExtra("year", semester.yearStr)
                    putExtra("term", semester.termStr)
                }
                startActivityForResult(intent, ACTIVITY_SCORE_FILTER)
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                mViewModel.refreshScoreList()
            }
            R.id.menu_filter -> {
                val semester = mViewModel.semester.value
                if (semester == null) {
                    toast("未找到学期信息")
                    return true
                }
//                val action = ScoreListFragmentDirections
//                        .actionFragmentScoreListToFragmentScoreFilter(semester.yearStr, semester.termStr)
//                findNavController().navigate(action)
                val intent = Intent(requireContext(), ScoreFilterActivity::class.java).apply {
                    putExtra("year", semester.yearStr)
                    putExtra("term", semester.termStr)
                }
                startActivityForResult(intent, ACTIVITY_SCORE_FILTER)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("requestCode = [${requestCode}], resultCode = [${resultCode}], data = [${data}]")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_SCORE_FILTER) {
            mViewModel.updateIES()
        }
    }
}