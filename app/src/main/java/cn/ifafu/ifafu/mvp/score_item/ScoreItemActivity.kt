package cn.ifafu.ifafu.mvp.score_item

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.ViewModelProvider
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.ScoreItemActivityBinding
import cn.ifafu.ifafu.view.adapter.ScoreItemAdapter
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.score_item_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScoreItemActivity : BaseActivity<ScoreItemActivityBinding, ScoreItemViewModel>() {

    override fun getViewModel(): ScoreItemViewModel {
        return ViewModelProvider(this).get(ScoreItemViewModel::class.java)
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
        mViewModel.init(intent.getLongExtra("id", 0L)) {
            withContext(Dispatchers.Main) {
                adapter.replaceData(it)
            }
        }
    }

}