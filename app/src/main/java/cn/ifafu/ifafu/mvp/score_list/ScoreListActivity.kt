package cn.ifafu.ifafu.mvp.score_list

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.mvp.score_item.ScoreItemActivity
import cn.ifafu.ifafu.view.adapter.ScoreAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider
import cn.ifafu.ifafu.view.dialog.ProgressDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.score_list_activity.*

class ScoreListActivity : BaseActivity<ScoreListContract.Presenter>(), ScoreListContract.View, View.OnClickListener {

    private var mAdapter: ScoreAdapter? = null
    private lateinit var progressDialog: ProgressDialog
    private var yearTermOPV: OptionsPickerView<String>? = null

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.score_list_activity
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_score)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        mPresenter = ScoreListPresenter(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setOnCancelListener {
            mPresenter?.cancelLoading()
        }

        tv_score_title.setOnClickListener(this)
        btn_refresh.setOnClickListener(this)
        layout_ies.setOnClickListener(this)
        layout_cnt.setOnClickListener(this)
    }

    override fun setYearTermOptions(option1: Int, option2: Int) {
        yearTermOPV!!.setSelectOptions(option1, option2)
    }

    override fun setRvScoreData(data: List<Score>) {
        if (data.isEmpty()) {
            view_exam_empty.visibility = View.VISIBLE
            rv_score.visibility = View.GONE
        } else {
            view_exam_empty.visibility = View.GONE
            rv_score.visibility = View.VISIBLE
            if (mAdapter == null) {
                mAdapter = ScoreAdapter(this, data)
                mAdapter!!.setOnScoreClickListener { score: Score ->
                    val intent = Intent(this, ScoreItemActivity::class.java)
                    intent.putExtra("id", score.id)
                    startActivity(intent)
                }
                rv_score.layoutManager = LinearLayoutManager(this)
                rv_score.addItemDecoration(RecyclerViewDivider(
                        this, LinearLayoutManager.VERTICAL, R.drawable.shape_divider))
                rv_score.adapter = mAdapter
            } else {
                mAdapter!!.setScoreData(data)
                mAdapter!!.notifyDataSetChanged()
            }
        }
        tv_cnt_big.text = data.size.toString()
        tv_cnt_little.text = "门"
    }

    override fun setYearTermData(years: List<String>, terms: List<String>) {
        if (yearTermOPV == null) {
            yearTermOPV = OptionsPickerBuilder(this,
                    OnOptionsSelectListener { options1: Int, options2: Int, _: Int, _: View? ->
                        setYearTermTitle(years[options1], terms[options2])
                        mPresenter!!.switchYearTerm(options1, options2)
                    })
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .setTitleText("请选择学年与学期")
                    .setTitleColor(Color.parseColor("#157efb"))
                    .setTitleSize(13)
                    .build()
        }
        yearTermOPV!!.setNPicker(years, terms, null)
    }

    override fun setIESText(big: String, little: String) {
        tv_ies_big.text = big
        tv_ies_little.text = little
    }

    override fun setCntText(big: String, little: String) {
        tv_cnt_big.text = big
        tv_cnt_little.text = little
    }

    override fun setGPAText(text: String) {
        tv_gpa.text = text
    }

    override fun setYearTermTitle(year: String, term: String) {
        if (term == "全部") {
            tv_score_title.text = String.format("%s学年全部学习成绩", year)
        } else {
            tv_score_title.text = String.format("%s学年第%s学期学习成绩", year, term)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_score_title -> yearTermOPV!!.show()
            R.id.layout_ies -> mPresenter.checkIESDetail();
            R.id.layout_cnt -> mPresenter.openFilterActivity()
            R.id.btn_refresh -> mPresenter.updateFromNet()
        }
    }

    override fun showIESDetail(text: String) {
        MaterialDialog(this).show {
            title(text = "智育分计算详情")
            message(text = text)
            negativeButton(text = "智育分计算规则") {
                MaterialDialog(this@ScoreListActivity).show {
                    title(text = "智育分计算规则")
                    message(res = R.string.score_ies_rule)
                    positiveButton(text = "收到")
                }
            }
            positiveButton(text = "好嘞~")
        }
    }

    override fun showLoading() {
        progressDialog!!.show()
    }

    override fun hideLoading() {
        progressDialog!!.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.ACTIVITY_SCORE_FILTER) {
            mPresenter.updateIES()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}