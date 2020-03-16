package cn.ifafu.ifafu.ui.score_item

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ScoreItemActivityBinding
import cn.ifafu.ifafu.view.adapter.ScoreItemAdapter
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.score_item_activity.*

class ScoreItemActivity : BaseActivity<ScoreItemActivityBinding, ScoreItemViewModel>() {

    override fun getViewModel(): ScoreItemViewModel {
        return VMProvider(this).get(ScoreItemViewModel::class.java)
    }

    private val adapter by lazy { ScoreItemAdapter() }

    override fun getLayoutId(): Int = R.layout.score_item_activity

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_score_item)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        val layoutManager = GridLayoutManager(this, 2)
        mBinding.layoutManager = layoutManager
        mBinding.adapter = adapter
        mViewModel.score.observe(this, Observer {
            adapter.replaceData(it)
        })
        mViewModel.init(intent.getLongExtra("id", 0L))
    }

}