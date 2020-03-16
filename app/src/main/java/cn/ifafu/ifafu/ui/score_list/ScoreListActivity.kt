package cn.ifafu.ifafu.ui.score_list

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ScoreListActivityBinding
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.ui.score_filter.ScoreFilterActivity
import cn.ifafu.ifafu.ui.score_item.ScoreItemActivity
import cn.ifafu.ifafu.view.adapter.ScoreAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider
import cn.ifafu.ifafu.view.dialog.LoadingDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.score_list_activity.*

class ScoreListActivity : BaseActivity<ScoreListActivityBinding, ScoreListViewModel>(), View.OnClickListener {

    private val mAdapter: ScoreAdapter by lazy {
        ScoreAdapter(this).apply {
            setOnScoreClickListener { score: Score ->
                val intent = Intent(this@ScoreListActivity, ScoreItemActivity::class.java)
                intent.putExtra("id", score.id)
                startActivity(intent)
            }
        }
    }
    private val iesDetailDialog by lazy {
        MaterialDialog(this@ScoreListActivity).apply {
            title(text = "智育分计算详情")
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

    override val mLoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("获取中")
            setCancelable(true)
        }
    }

    private var first = true

    override fun getViewModel(): ScoreListViewModel {
        return VMProvider(this).get(ScoreListViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.score_list_activity

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_score)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        mBinding.rvScore.addItemDecoration(RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL, R.drawable.shape_divider))
        mBinding.adapter = mAdapter
        mViewModel.semester.observe(this, Observer {
            if (first) {
                mYearTermOptionPicker.setNPicker(it.yearList, it.termList, null)
                mYearTermOptionPicker.setSelectOptions(it.yearIndex, it.termIndex)
                mBinding.tvScoreTitle.setOnClickListener(this@ScoreListActivity)
                first = false
            }
            mBinding.title =  if (it.termStr == "全部" && it.yearStr == "全部") {
                "全部学习成绩"
            } else if (it.termStr == "全部") {
                "${it.yearStr}学年全部学期"
            } else {
                "${it.yearStr}学年第${it.termStr}学期学习成绩"
            }
        })
        mViewModel.scoreList.observe(this, Observer {
            mBinding.empty = it.isEmpty()
            mAdapter.scoreList = it
            mAdapter.notifyDataSetChanged()
        })
        mViewModel.cnt.observe(this, Observer {
            mBinding.cnt = it
        })
        mViewModel.gpa.observe(this, Observer {
            mBinding.gpa = it
        })
        mViewModel.ies.observe(this, Observer {
            mBinding.ies = it
        })
        mViewModel.iesDetail.observe(this, Observer {
            iesDetailDialog.show {
                message(text = it)
            }
        })
        btn_refresh.setOnClickListener(this)
        layout_ies.setOnClickListener(this)
        layout_cnt.setOnClickListener(this)
        mViewModel.initData()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_score_title -> mYearTermOptionPicker.show()
            R.id.layout_ies -> mViewModel.iesCalculationDetail()
            R.id.layout_cnt ->  {
                val intent = Intent(this@ScoreListActivity, ScoreFilterActivity::class.java)
                val semester = mViewModel.semester.value!!
                intent.putExtra("year", semester.yearStr)
                intent.putExtra("term", semester.termStr)
                startActivityForResult(intent, Constant.ACTIVITY_SCORE_FILTER)
            }
            R.id.btn_refresh -> mViewModel.refreshScoreList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.ACTIVITY_SCORE_FILTER) {
            mViewModel.updateIES()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}