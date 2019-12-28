package cn.ifafu.ifafu.mvp.setting

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.ViewModelFactory
import cn.ifafu.ifafu.base.mvvm.BaseActivity
import cn.ifafu.ifafu.databinding.SettingActivityBinding
import cn.ifafu.ifafu.view.adapter.syllabus_setting.*
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.setting_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.drakeet.multitype.MultiTypeAdapter

class SettingActivity : BaseActivity<SettingActivityBinding>() {

    private val adapter by lazy {
        MultiTypeAdapter().apply {
            register(SeekBarItem::class, SeekBarBinder())
            register(CheckBoxItem::class, CheckBoxBinder())
            register(TextViewItem::class, TextViewBinder())
            register(ColorItem::class, ColorBinder())
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this, ViewModelFactory)
                .get(SettingViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.setting_activity

    override fun initActivity(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
                .titleBarMarginTop(tb_setting)
                .statusBarDarkFont(true)
                .statusBarColor("#FFFFFF")
                .init()
        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.shape_divider)!!)
        mBinding.rvSetting.addItemDecoration(dividerItemDecoration)
        mBinding.layoutManager = LinearLayoutManager(this)
        mBinding.adapter = adapter
        viewModel.initSetting {
            withContext(Dispatchers.Main) {
                adapter.items = it
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun finish() {
        viewModel.ifNeedCheckTheme {
            setResult(Activity.RESULT_OK)
        }
        super.finish()
    }
}