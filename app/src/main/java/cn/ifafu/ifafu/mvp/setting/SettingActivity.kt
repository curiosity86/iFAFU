package cn.ifafu.ifafu.mvp.setting

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.view.adapter.syllabus_setting.*
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_setting.*
import me.drakeet.multitype.MultiTypeAdapter

class SettingActivity : BaseActivity<SettingContract.Presenter>(), SettingContract.View {

    override fun getLayoutId(savedInstanceState: Bundle?): Int {
        return R.layout.activity_setting
    }

    override fun initData(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tv_setting)
                .statusBarDarkFont(true)
                .statusBarColor("#FFFFFF")
                .init()
        mPresenter = SettingPresenter(this)
    }

    override fun initRecycleView(items: List<Any>) {
        val adapter = MultiTypeAdapter()
        adapter.register(SeekBarItem::class, SeekBarBinder())
        adapter.register(CheckBoxItem::class, CheckBoxBinder())
        adapter.register(TextViewItem::class, TextViewBinder())
        adapter.register(ColorItem::class, ColorBinder())
        adapter.items = items
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        context.getDrawable(R.drawable.shape_divider)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        rv_setting.addItemDecoration(dividerItemDecoration)
        rv_setting.layoutManager = LinearLayoutManager(this)
        rv_setting.adapter = adapter
    }

    override fun finish() {
        mPresenter.onFinish()
        super.finish()
    }
}