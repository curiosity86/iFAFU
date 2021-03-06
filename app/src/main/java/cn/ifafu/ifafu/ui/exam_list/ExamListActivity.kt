package cn.ifafu.ifafu.ui.exam_list

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityExamListBinding
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.ui.view.SemesterPicker
import cn.ifafu.ifafu.ui.view.adapter.ExamAdapter
import kotlinx.android.synthetic.main.activity_exam_list.*

class ExamListActivity : BaseActivity() {

    private val mExamAdapter = ExamAdapter()
    private val mLoadingDialog = LoadingDialog(this)
    private val mViewModel: ExamListViewModel by viewModels { getViewModelFactory() }

    private val mSemesterOptionPicker by lazy {
        SemesterPicker(this) { year, term ->
            mViewModel.switchYearAndTerm(year, term)
        }
    }

    private lateinit var mBinding: ActivityExamListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        mBinding = bind(R.layout.activity_exam_list)

        //初始化监听事件
        btn_refresh.setOnClickListener { mViewModel.refresh() }
        tb_exam.setNavigationOnClickListener { finish() }
        tb_exam.setSubtitleClickListener { v ->
            mSemesterOptionPicker.show()
        }
        tb_exam.setSubtitleDrawablesRelative(null, null, this.getDrawable(R.drawable.ic_down_little), null)

        //初始化RecycleView
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val divider = this.getDrawable(R.drawable.shape_divider)!!
        dividerItemDecoration.setDrawable(divider)
        mBinding.vm = mViewModel
        rv_exam.addItemDecoration(dividerItemDecoration)
        rv_exam.adapter = mExamAdapter

        //初始化ViewModel
        mViewModel.exams.observe(this, Observer {
            isShowEmptyView(it.isEmpty())
            mExamAdapter.setNewInstance(it.toMutableList())
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
