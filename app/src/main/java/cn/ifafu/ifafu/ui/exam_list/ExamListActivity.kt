package cn.ifafu.ifafu.ui.exam_list

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ExamListActivityBinding
import cn.ifafu.ifafu.view.adapter.ExamAdapter
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.exam_list_activity.*

class ExamListActivity : BaseActivity<ExamListActivityBinding, ExamListViewModel>() {

    private val mExamAdapter: ExamAdapter by lazy {
        ExamAdapter(this)
    }

    override val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("刷新中")
        }
    }

    private val mYearTermOptionPicker: OptionsPickerView<String> by lazy {
        OptionsPickerBuilder(this,
                OnOptionsSelectListener { options1, options2, _, _ ->
                    mViewModel.switchYearAndTerm(options1, options2)
                })
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("请选择学年与学期")
                .setTitleColor(Color.parseColor("#157efb"))
                .setTitleSize(13)
                .build<String>()
    }

    private var first = true

    override fun getLayoutId(): Int {
        return R.layout.exam_list_activity
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
        btn_refresh.setOnClickListener { mViewModel.update() }
        tb_exam.setNavigationOnClickListener { finish() }
        tb_exam.setSubtitleClickListener { v -> mYearTermOptionPicker.show() }
        tb_exam.setSubtitleDrawablesRelative(null, null, this.getDrawable(R.drawable.ic_down_little), null)
        rv_exam.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        val divider = this.getDrawable(R.drawable.shape_divider)
        if (divider != null) {
            dividerItemDecoration.setDrawable(divider)
        }
        rv_exam.addItemDecoration(dividerItemDecoration)
        rv_exam.adapter = mExamAdapter
        mViewModel.examList.observe(this, Observer {
            isShowEmptyView(it.isEmpty())
            mExamAdapter.data = it
            mExamAdapter.notifyDataSetChanged()
        })

        mViewModel.semester.observe(this, Observer {
            if (first) {
                mYearTermOptionPicker.setNPicker(it.yearList, it.termList, null)
                mYearTermOptionPicker.setSelectOptions(it.yearIndex, it.termIndex)
                first = false
            }
            tb_exam.subtitle =  if (it.termStr == "全部" && it.yearStr == "全部") {
                "全部考试信息"
            } else if (it.termStr == "全部") {
                "${it.yearStr}学年全部学期"
            } else {
                "${it.yearStr}学年第${it.termStr}学期"
            }
        })
        mViewModel.initData()
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
