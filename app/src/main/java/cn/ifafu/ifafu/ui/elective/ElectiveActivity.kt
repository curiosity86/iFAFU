package cn.ifafu.ifafu.ui.elective

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ElectiveActivityBinding
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.ui.score_item.ScoreItemActivity
import cn.ifafu.ifafu.view.adapter.ElectiveAdapter
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.elective_activity.*

class ElectiveActivity : BaseActivity<ElectiveActivityBinding, ElectiveViewModel>(), View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.elective_activity
    }

    override fun getViewModel(): ElectiveViewModel {
       return VMProvider(this)[ElectiveViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(mBinding.tbElective)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        val click = { score: Score ->
            val intent = Intent(this@ElectiveActivity, ScoreItemActivity::class.java)
            intent.putExtra("id", score.id)
            startActivity(intent)
        }
        mViewModel.total.observe(this, Observer {
            mBinding.total = it
            mBinding.totalAdapter = ElectiveAdapter(it.scores, click)
        })
        mViewModel.zrkx.observe(this, Observer {
            mBinding.zrkx = it
            mBinding.zrkxAdapter = ElectiveAdapter(it.scores, click)
        })
        mViewModel.rwsk.observe(this, Observer {
            mBinding.rwsk = it
            mBinding.rwskAdapter = ElectiveAdapter(it.scores, click)
        })
        mViewModel.ysty.observe(this, Observer {
            mBinding.ysty = it
            mBinding.ystyAdapter = ElectiveAdapter(it.scores, click)
        })
        mViewModel.wxsy.observe(this, Observer {
            mBinding.wxsy = it
            mBinding.wxsyAdapter = ElectiveAdapter(it.scores, click)
        })
        mViewModel.cxcy.observe(this, Observer {
            mBinding.cxcy = it
            mBinding.cxcyAdapter = ElectiveAdapter(it.scores, click)
        })
        mViewModel.init()
        initView()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.layout_total -> mBinding.totalShowMore = !(mBinding.totalShowMore ?: true)
            R.id.layout_zrkx -> mBinding.zrkxShowMore = !(mBinding.zrkxShowMore ?: true)
            R.id.layout_rwsk -> mBinding.rwskShowMore = !(mBinding.rwskShowMore ?: true)
            R.id.layout_ysty -> mBinding.ystyShowMore = !(mBinding.ystyShowMore ?: true)
            R.id.layout_wxsy -> mBinding.wxsyShowMore = !(mBinding.wxsyShowMore ?: true)
            R.id.layout_cxcy -> mBinding.cxcyShowMore = !(mBinding.cxcyShowMore ?: true)
        }
    }

    private fun initView() {
        mBinding.totalShowMore = false
        mBinding.zrkxShowMore = false
        mBinding.rwskShowMore = false
        mBinding.ystyShowMore = false
        mBinding.wxsyShowMore = false
        mBinding.cxcyShowMore = false
        layout_total.setOnClickListener(this)
        layout_zrkx.setOnClickListener(this)
        layout_rwsk.setOnClickListener(this)
        layout_ysty.setOnClickListener(this)
        layout_wxsy.setOnClickListener(this)
        layout_cxcy.setOnClickListener(this)
    }

}