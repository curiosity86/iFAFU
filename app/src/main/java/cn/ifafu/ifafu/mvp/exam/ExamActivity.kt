package cn.ifafu.ifafu.mvp.exam

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.view.adapter.ExamAdapter
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_exam.*

class ExamActivity : BaseActivity<ExamContract.Presenter>(), ExamContract.View {

    private var examAdapter: ExamAdapter? = null

    private lateinit var progressDialog: ProgressDialog
    private var yearTermOPV: OptionsPickerView<String>? = null

    private var years: List<String>? = null
    private var terms: List<String>? = null

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_exam
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_exam)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        mPresenter = ExamPresenter(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setOnCancelListener { mPresenter.cancelLoading() }

        btn_refresh.setOnClickListener { mPresenter.update() }
        tb_exam.setNavigationOnClickListener { finish() }

    }

    override fun setExamAdapterData(data: List<Exam>) {
        if (examAdapter == null) {
            examAdapter = ExamAdapter(this, data)
            rv_exam.layoutManager = LinearLayoutManager(this)
            val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            val divider = context.getDrawable(R.drawable.shape_divider)
            if (divider != null) {
                dividerItemDecoration.setDrawable(divider)
            }
            rv_exam.addItemDecoration(dividerItemDecoration)
            rv_exam.adapter = examAdapter
        } else if (data.isNotEmpty()) {
            examAdapter!!.setExamData(data)
            examAdapter!!.notifyDataSetChanged()
        }

        isShowEmptyView(data.isEmpty())
    }

    override fun setYearTermData(years: List<String>, terms: List<String>) {
        this.years = years
        this.terms = terms
        if (yearTermOPV == null) {
            yearTermOPV = OptionsPickerBuilder(this) { options1, options2, options3, v ->
                setSubtitle(years[options1], terms[options2])
                mPresenter.switchYearTerm(years[options1], terms[options2])
            }
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("请选择学年与学期")
                    .setTitleColor(Color.parseColor("#157efb"))
                    .setTitleSize(13)
                    .build()
            tb_exam.setSubtitleClickListener { v -> yearTermOPV!!.show() }
            tb_exam.setSubtitleDrawablesRelative(null, null, context.getDrawable(R.drawable.ic_down_little), null
            )
        }
        yearTermOPV!!.setNPicker(years, terms, null)
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


    override fun showEmptyView() {
        isShowEmptyView(true)
    }

    override fun setYearTermOptions(option1: Int, option2: Int) {
        yearTermOPV!!.setSelectOptions(option1, option2)
        setSubtitle(years!![option1], terms!![option2])
    }

    override fun showLoading() {
        progressDialog.show()
    }

    override fun hideLoading() {
        progressDialog.cancel()
    }

    private fun setSubtitle(year: String, term: String) {
        tb_exam.subtitle = String.format("%s学年第%s学期", year, term)
    }


}
