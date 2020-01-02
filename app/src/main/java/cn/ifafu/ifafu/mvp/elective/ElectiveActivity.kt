package cn.ifafu.ifafu.mvp.elective

import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.ViewModelProvider
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.ElectiveActivityBinding
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.mvp.score_item.ScoreItemActivity
import cn.ifafu.ifafu.view.adapter.ElectiveAdapter
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.elective_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectiveActivity : BaseActivity<ElectiveActivityBinding, ElectiveViewModel>(), View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.elective_activity
    }

    override fun getViewModel(): ElectiveViewModel {
       return ViewModelProvider(this)[ElectiveViewModel::class.java]
    }

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(mBinding.tbElective)
                .statusBarColor("#FFFFFF")
                .statusBarDarkFont(true)
                .init()
        mViewModel.init { total, zrkx, rwsk, ysty, wxsy, cxcy ->
            withContext(Dispatchers.Main) {
                val click = { score: Score ->
                    val intent = Intent(this@ElectiveActivity, ScoreItemActivity::class.java)
                    intent.putExtra("id", score.id)
                    startActivity(intent)
                }
                mBinding.total = total
                mBinding.totalAdapter = ElectiveAdapter(total.scores, click)
                zrkx?.run {
                    mBinding.zrkx = this
                    mBinding.zrkxAdapter = ElectiveAdapter(scores, click)
                }
                rwsk?.run {
                    mBinding.rwsk = this
                    mBinding.rwskAdapter = ElectiveAdapter(scores, click)
                }
                ysty?.run {
                    mBinding.ysty = this
                    mBinding.ystyAdapter = ElectiveAdapter(scores, click)
                }
                wxsy?.run {
                    mBinding.wxsy = this
                    mBinding.wxsyAdapter = ElectiveAdapter(scores, click)
                }
                cxcy?.run {
                    mBinding.cxcy = this
                    mBinding.cxcyAdapter = ElectiveAdapter(scores, click)
                }
            }
        }
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
        mBinding.totalShowMore = false
        layout_total.setOnClickListener(this)
        layout_zrkx.setOnClickListener(this)
        layout_rwsk.setOnClickListener(this)
        layout_ysty.setOnClickListener(this)
        layout_wxsy.setOnClickListener(this)
        layout_cxcy.setOnClickListener(this)
    }

}