package cn.ifafu.ifafu.ui.setting

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseSimpleActivity
import cn.ifafu.ifafu.data.entity.GlobalSetting
import cn.ifafu.ifafu.databinding.ActivitySettingBinding
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_setting.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingActivity : BaseSimpleActivity() {

    private val mAdapter = SettingAdapter()

    private val mViewModel by viewModel<SettingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
                .titleBar(tb_setting)
                .statusBarDarkFont(true)
                .init()
        val binding = DataBindingUtil.setContentView<ActivitySettingBinding>(this, R.layout.activity_setting)

        // 初始化RecycleView
        val dividerItemDecoration = DividerItemDecoration(this@SettingActivity, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.shape_divider)!!)
        binding.rvSetting.addItemDecoration(dividerItemDecoration)
        binding.adapter = mAdapter

        // 初始化ViewModel
        mViewModel.settings.observe(this, Observer { setting ->
            mAdapter.setNewInstance(mutableListOf(
                    SettingItem.CheckBox("旧版主页主题", "应需求而来，喜欢0.9版本iFAFU界面就快来呀", setting.theme == GlobalSetting.THEME_OLD) {
                        setting.theme = if (it) GlobalSetting.THEME_OLD else GlobalSetting.THEME_NEW
                        mViewModel.save(setting)
                    }))
        })
    }
}