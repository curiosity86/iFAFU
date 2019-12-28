package cn.ifafu.ifafu.mvp.score_filter

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.ViewModelFactory
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.ScoreFilterActivityBinding
import cn.ifafu.ifafu.view.adapter.ScoreFilterAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider
import com.jaeger.library.StatusBarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScoreFilterActivity : BaseActivity<ScoreFilterActivityBinding>() {

    private val mAdapter by lazy { ScoreFilterAdapter(this) }

    private val mViewModel: ScoreFilterViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory).get(ScoreFilterViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.score_filter_activity

    override fun initActivity(savedInstanceState: Bundle?) {
        StatusBarUtil.setTransparent(this)
        StatusBarUtil.setLightMode(this)
        mBinding.adapter = mAdapter
        mBinding.layoutManager = LinearLayoutManager(this)
        mBinding.rvScoreFilter.addItemDecoration(RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL, R.drawable.shape_divider))

        mViewModel.init(this, { list, ies ->
            withContext(Dispatchers.Main) {
                mAdapter.data = list
                mBinding.ies = ies
                mAdapter.notifyDataSetChanged()
            }
        }, this::showMessage)

        mBinding.setFilterAllAction {
            mAdapter.setAllChecked()
            mViewModel.allChecked { ies ->
                withContext(Dispatchers.Main) {
                    mBinding.ies = ies
                }
            }
        }

        mAdapter.afterCheckedListener = {
            mViewModel.itemChecked(it) { ies ->
                withContext(Dispatchers.Main) {
                    mBinding.ies = ies
                }
            }
        }

    }

}
