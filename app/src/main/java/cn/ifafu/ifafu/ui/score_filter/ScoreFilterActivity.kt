package cn.ifafu.ifafu.ui.score_filter

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivityScoreFilterBinding
import cn.ifafu.ifafu.view.adapter.ScoreFilterAdapter
import cn.ifafu.ifafu.view.custom.RecyclerViewDivider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScoreFilterActivity : BaseActivity() {

    private val mAdapter by lazy { ScoreFilterAdapter(this) }

    private val mViewModel: ScoreFilterViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        val mBinding = bind<ActivityScoreFilterBinding>(R.layout.activity_score_filter)
        mBinding.adapter = mAdapter
        mBinding.layoutManager = LinearLayoutManager(this)
        mBinding.rvScoreFilter.addItemDecoration(RecyclerViewDivider(
                this, LinearLayoutManager.VERTICAL, R.drawable.shape_divider))

        mViewModel.init(this) { list, ies ->
            withContext(Dispatchers.Main) {
                mAdapter.data = list
                mBinding.ies = ies
            }
        }

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
