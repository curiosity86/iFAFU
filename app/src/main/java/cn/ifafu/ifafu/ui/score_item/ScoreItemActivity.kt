package cn.ifafu.ifafu.ui.score_item

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityScoreItemBinding
import cn.ifafu.ifafu.view.adapter.ScoreItemAdapter

class ScoreItemActivity : BaseActivity() {

    private val mViewModel: ScoreItemViewModel by viewModels { getViewModelFactory() }

    private val mAdapter by lazy { ScoreItemAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        with(bind<ActivityScoreItemBinding>(R.layout.activity_score_item)) {
            layoutManager = GridLayoutManager(this@ScoreItemActivity, 2)
            adapter = mAdapter
        }
        mViewModel.score.observe(this, Observer {
            mAdapter.replaceData(it)
        })
        mViewModel.init(intent.getLongExtra("id", 0L))
    }

}