package cn.ifafu.ifafu.mvp.score_list

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.ViewModelProvider
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.ScoreListActivityBinding
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.mvp.score_filter.ScoreFilterActivity
import cn.ifafu.ifafu.mvp.score_item.ScoreItemActivity
import cn.ifafu.ifafu.view.adapter.ScoreAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider
import com.afollestad.materialdialogs.MaterialDialog
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.score_list_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    private val mYearTermOptionPicker: OptionsPickerView<String> by lazy {
        OptionsPickerBuilder(this,
                OnOptionsSelectListener { options1, options2, _, _ ->
                    mViewModel.switchYearAndTerm(options1, options2) { scores, ies, cnt, gpa, title ->
                        showScoreInfo(scores, ies, cnt, gpa)
                        withContext(Dispatchers.Main) {
                            mBinding.title = title
                        }
                    }
                })
                .setCancelText("取消")
                .setSubmitText("确定")
                .setTitleText("请选择学年与学期")
                .setTitleColor(Color.parseColor("#157efb"))
                .setTitleSize(13)
                .build<String>()
    }

    override fun getViewModel(): ScoreListViewModel {
        return ViewModelProvider(this).get(ScoreListViewModel::class.java)
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

        mViewModel.initScoreList { scores, ies, cnt, gpa ->
            showScoreInfo(scores, ies, cnt, gpa)
        }
        mViewModel.initOptionPickerData { yearList, termList, yearIndex, termIndex, title ->
            withContext(Dispatchers.Main) {
                mYearTermOptionPicker.setNPicker(yearList, termList, null)
                mYearTermOptionPicker.setSelectOptions(yearIndex, termIndex)
                mBinding.tvScoreTitle.setOnClickListener(this@ScoreListActivity)
                mBinding.title = title
            }
        }

        btn_refresh.setOnClickListener(this)
        layout_ies.setOnClickListener(this)
        layout_cnt.setOnClickListener(this)
    }

    private suspend fun showScoreInfo(scores: List<Score>,
                                      ies: Pair<String, String>,
                                      cnt: Pair<String, String>,
                                      gpa: String) = withContext(Dispatchers.Main) {
        mBinding.empty = scores.isEmpty()
        mAdapter.scoreList = scores
        mBinding.ies = ies
        mBinding.cnt = cnt
        mBinding.gpa = gpa
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_score_title -> mYearTermOptionPicker.show()
            R.id.layout_ies -> mViewModel.iesCalculationDetail { showIESDetail(it) }
            R.id.layout_cnt -> mViewModel.getYearAndTerm { year, term ->
                val intent = Intent(this@ScoreListActivity, ScoreFilterActivity::class.java)
                intent.putExtra("year", year)
                intent.putExtra("term", term)
                startActivityForResult(intent, Constant.ACTIVITY_SCORE_FILTER)
            }
            R.id.btn_refresh -> mViewModel.refreshScoreList { scores, ies, cnt, gpa ->
                showScoreInfo(scores, ies, cnt, gpa)
            }
        }
    }

    private suspend fun showIESDetail(text: String) = withContext(Dispatchers.Main) {
        MaterialDialog(this@ScoreListActivity).show {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constant.ACTIVITY_SCORE_FILTER) {
            mViewModel.updateIES {
                withContext(Dispatchers.Main) {
                    mBinding.ies = it
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}