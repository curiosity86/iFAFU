package cn.ifafu.ifafu.ui.exam_list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityExamListBinding
import cn.ifafu.ifafu.view.adapter.ExamAdapter
import cn.ifafu.ifafu.ui.view.LoadingDialog
import cn.ifafu.ifafu.ui.view.SemesterOptionPicker
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_exam_list.*

class ExamListActivity : BaseActivity<ActivityExamListBinding, ExamListViewModel>() {

    private val mExamAdapter = ExamAdapter(this)

    override val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this).apply { setText("获取中") }
    }

    private val mSemesterOptionPicker by lazy {
        SemesterOptionPicker(this) { year, term ->
            mViewModel.switchYearAndTerm(year, term)
        }
    }

    private var first = true

    override fun getLayoutId(): Int {
        return R.layout.activity_exam_list
    }

    override fun getViewModel(): ExamListViewModel? {
        return VMProvider(this)[ExamListViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_exam)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
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
        mViewModel.toastMessage.observe(this, Observer { toast(it) })
        mViewModel.loading.observe(this, Observer {
            with(mLoadingDialog) {
                if (it) show() else cancel()
            }
        })
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
