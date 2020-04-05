package cn.ifafu.ifafu.ui.exam_list

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityExamListBinding
import cn.ifafu.ifafu.ui.view.adapter.ExamAdapter
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.ui.view.SemesterOptionPicker
import kotlinx.android.synthetic.main.activity_exam_list.*

class ExamListActivity : BaseActivity() {

    private val mExamAdapter = ExamAdapter(this)
    private val mLoadingDialog = LoadingDialog(this)
    private val mViewModel: ExamListViewModel by viewModels { getViewModelFactory() }

    private val mSemesterOptionPicker by lazy {
        SemesterOptionPicker(this) { year, term ->
            mViewModel.switchYearAndTerm(year, term)
        }
    }

    private var first = true
    private lateinit var mBinding: ActivityExamListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        mBinding = bind(R.layout.activity_exam_list)
        btn_refresh.setOnClickListener { mViewModel.refresh() }
        tb_exam.setNavigationOnClickListener { finish() }
        tb_exam.setSubtitleClickListener { v ->
            mViewModel.semester.value?.run {
                mSemesterOptionPicker.setSemester(this)
                mSemesterOptionPicker.show()
            }
        }
        tb_exam.setSubtitleDrawablesRelative(null, null, this.getDrawable(R.drawable.ic_down_little), null)
        rv_exam.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val divider = this.getDrawable(R.drawable.shape_divider)
        if (divider != null) {
            dividerItemDecoration.setDrawable(divider)
        }
        mBinding.vm = mViewModel
        rv_exam.addItemDecoration(dividerItemDecoration)
        rv_exam.adapter = mExamAdapter
        mViewModel.exams.observe(this, Observer {
            isShowEmptyView(it.isEmpty())
            mExamAdapter.data = it
            mExamAdapter.notifyDataSetChanged()
        })
        mLoadingDialog.observe(this, mViewModel.loading)
    }

    private fun isShowEmptyView(show: Boolean) {
        if (show) {
            view_exam_empty.visibility = View.VISIBLE
            rv_exam.visibility = View.GONE
        } else {
            view_exam_empty.visibility = View.GONE
            rv_exam.visibility = View.VISIBLE
        }
    }

}
