package cn.ifafu.ifafu.mvp.score_filter

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.view.adapter.ScoreFilterAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_score_filter.*

class ScoreFilterActivity : BaseActivity<ScoreFilterConstant.Presenter>(), ScoreFilterConstant.View {

    private lateinit var mAdapter: ScoreFilterAdapter

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_score_filter
    }

    override fun initData(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        StatusBarUtil.setLightMode(this)
        mPresenter = ScoreFilterPresenter(this)

        mAdapter = ScoreFilterAdapter(this)
        mAdapter.setOnCheckedListener(object : ScoreFilterAdapter.OnCheckedListener {
            override fun onCheckedChanged(v: View?, item: Score, isChecked: Boolean) {
                mPresenter.onCheck(item, isChecked)
            }
        })
        rv_score_filter.adapter = mAdapter
        rv_score_filter.layoutManager = LinearLayoutManager(this)
        rv_score_filter.addItemDecoration(RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL, R.drawable.shape_divider))

        tb_score_filter.setNavigationOnClickListener { finish() }

        btn_filter_all.setOnClickListener {
            mAdapter.setAllChecked()
            mAdapter.notifyDataSetChanged()
            mPresenter.updateIES()
        }
    }

    override fun setAdapterData(list: List<Score>) {
        mAdapter.data = list
        mAdapter.notifyDataSetChanged()
    }

    override fun setIES(ies: String) {
        tv_now_ies.text = getString(R.string.score_filter_now_ies, ies)
    }

}
