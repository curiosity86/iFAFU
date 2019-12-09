package cn.ifafu.ifafu.mvp.score_item

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import cn.ifafu.ifafu.R.layout
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.view.adapter.ScoreItemAdapter
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.score_item_activity.*

class ScoreItemActivity : BaseActivity<ScoreItemConstant.Presenter>(), ScoreItemConstant.View {

    private var mAdapter: ScoreItemAdapter? = null

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return layout.score_item_activity
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_score_item)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()

        mPresenter = ScoreItemPresenter(this)
    }

    override fun setRvData(map: Map<String, String>) {
        if (mAdapter == null) {
            mAdapter = ScoreItemAdapter(map)
            val layoutManager = GridLayoutManager(this, 2)
            rv_score_item.layoutManager = layoutManager
            rv_score_item.adapter = mAdapter
        } else {
            mAdapter!!.replaceData(map.toList())
            mAdapter!!.notifyDataSetChanged()
        }
    }
}