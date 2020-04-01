package cn.ifafu.ifafu.experiment.score.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.databinding.FragmentScoreListBinding
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.ui.view.SemesterOptionPicker
import cn.ifafu.ifafu.view.adapter.ScoreAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider
import com.afollestad.materialdialogs.MaterialDialog
import timber.log.Timber

class ScoreListFragment : Fragment(), View.OnClickListener {

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
        return (mBinding ?: FragmentScoreListBinding.inflate(inflater, container, false).apply {
            Timber.d("123123")
            vm = mViewModel
            rvScore.addItemDecoration(RecyclerViewDivider(
                    requireContext(), LinearLayoutManager.VERTICAL, R.drawable.shape_divider))
            tvScoreTitle.setOnClickListener(this@ScoreListFragment)
            layoutIes.setOnClickListener(this@ScoreListFragment)
            layoutCnt.setOnClickListener(this@ScoreListFragment)
            rvScore.apply {
                adapter = mAdapter
            }
            toolbar.setNavigationOnClickListener {
                activity?.finish()
            }
            lifecycleOwner = viewLifecycleOwner
            mViewModel.iesDetail.observe(viewLifecycleOwner, Observer {
                Timber.d("ies detail")
                iesDetailDialog.show { message(text = it) }
            })
            mViewModel.scoreList.observe(viewLifecycleOwner, Observer {
                Timber.d("score list")
                mAdapter.scoreList = it
                mAdapter.notifyDataSetChanged()
            })
            mLoadingDialog.observe(viewLifecycleOwner, mViewModel.loading)
        }.also {
            mBinding = it
        }).root
    }

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
                val semester = mViewModel.semester.value ?: return
                val action = ScoreListFragmentDirections
                        .actionFragmentScoreListToFragmentScoreFilter(semester.yearStr, semester.termStr)
                findNavController().navigate(action)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_refresh -> {
                mViewModel.refreshScoreList()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}