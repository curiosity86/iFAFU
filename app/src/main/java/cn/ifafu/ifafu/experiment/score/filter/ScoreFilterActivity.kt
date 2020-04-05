package cn.ifafu.ifafu.experiment.score.filter

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseSimpleActivity
import cn.ifafu.ifafu.databinding.FragmentScoreFilterBinding
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.ui.view.adapter.ScoreFilterAdapter
import cn.ifafu.ifafu.ui.view.custom.RecyclerViewDivider
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.fragment_score_filter.*

// 暂时不适用Navigation + Fragment
// 原因：对于ScoreFilter页面结束后，通知ScoreList刷新成绩无较好的Fragment解决方案
// 目前方案：通过Activity#onActivityResult
class ScoreFilterActivity : BaseSimpleActivity(), Toolbar.OnMenuItemClickListener {

    private val mAdapter by lazy {
        ScoreFilterAdapter(this) {
            mViewModel.itemChecked(it)
        }
    }

    private val mViewModel: ScoreFilterViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<FragmentScoreFilterBinding>(this, R.layout.fragment_score_filter).apply {
            lifecycleOwner = this@ScoreFilterActivity
            vm = mViewModel
        }


        //沉浸状态栏
        ImmersionBar.with(this)
                .titleBar(tb_score_filter)
                .statusBarDarkFont(true)
                .init()

        //初始化监听事件
        tb_score_filter.setOnMenuItemClickListener(this)
        tb_score_filter.setNavigationOnClickListener {
            finish()
        }

        //初始化RecycleView
        rv_score_filter.adapter = mAdapter
        rv_score_filter.addItemDecoration(RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL, R.drawable.shape_divider))

        //初始化ViewModel
        val year = intent.getStringExtra("year")
        val term = intent.getStringExtra("term")
        if (year == null || term == null) {
            toast("无法找到学期信息")
            finish()
            return
        }
        mViewModel.init(year, term)
        mViewModel.scores.observe(this, Observer {
            mAdapter.data = it
        })
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_filter_all -> {
                mAdapter.setAllChecked()
                mViewModel.allChecked()
            }
        }
        return true
    }

}
