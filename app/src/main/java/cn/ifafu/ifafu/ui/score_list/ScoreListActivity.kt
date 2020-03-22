package cn.ifafu.ifafu.ui.score_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.databinding.ActivityScoreListBinding
import cn.ifafu.ifafu.ui.score_filter.ScoreFilterActivity
import cn.ifafu.ifafu.ui.score_item.ScoreItemActivity
import cn.ifafu.ifafu.ui.view.SemesterOptionPicker
import cn.ifafu.ifafu.view.adapter.ScoreAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider
import cn.ifafu.ifafu.ui.view.LoadingDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_score_list.*

class ScoreListActivity : BaseActivity<ActivityScoreListBinding, ScoreListViewModel>(), View.OnClickListener {

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
    private val mSemesterOptionPicker by lazy {
        SemesterOptionPicker(this) { year, term ->
            mViewModel.switchYearAndTerm(year, term)
        }
    }

    override val mLoadingDialog by lazy {
        LoadingDialog(this).apply {
            setText("获取中")
            setCancelable(true)
        }
    }

    override fun getViewModel(): ScoreListViewModel {
        return VMProvider(this).get(ScoreListViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.activity_score_list

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_score)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        with(mBinding) {
            adapter = mAdapter
            vm = mViewModel
            rvScore.addItemDecoration(RecyclerViewDivider(
                    this@ScoreListActivity, LinearLayoutManager.VERTICAL, R.drawable.shape_divider))
            btnRefresh.setOnClickListener(this@ScoreListActivity)
            layoutIes.setOnClickListener(this@ScoreListActivity)
            layoutCnt.setOnClickListener(this@ScoreListActivity)
        }
        mViewModel.iesDetail.observe(this, Observer {
            iesDetailDialog.show { message(text = it) }
        })
        mViewModel.scoreList.observe(this, Observer {
            mAdapter.scoreList = it
            mAdapter.notifyDataSetChanged()
        })
        mViewModel.initData()
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
            R.id.layout_cnt ->  {
                val intent = Intent(this@ScoreListActivity, ScoreFilterActivity::class.java)
                val semester = mViewModel.semester.value ?: return
                intent.putExtra("year", semester.yearStr)
                intent.putExtra("term", semester.termStr)
                startActivityForResult(intent, Constant.ACTIVITY_SCORE_FILTER)
            }
            R.id.btn_refresh -> mViewModel.refreshScoreList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.ACTIVITY_SCORE_FILTER) {
            //筛选智育分成绩后，更新智育分
            mViewModel.updateIES()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}