package cn.ifafu.ifafu.ui.setting

import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.ui.getViewModelFactory
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.databinding.ActivitySettingBinding
import cn.ifafu.ifafu.ui.view.adapter.syllabus_setting.*
import me.drakeet.multitype.MultiTypeAdapter

class SettingActivity : BaseActivity() {

    private val mAdapter by lazy {
        MultiTypeAdapter().apply {
            register(SeekBarItem::class, SeekBarBinder())
            register(CheckBoxItem::class, CheckBoxBinder())
            register(TextViewItem::class, TextViewBinder())
            register(ColorItem::class, ColorBinder())
        }
    }

    private val mViewModel: SettingViewModel by viewModels { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLightUiBar()
        with(bind<ActivitySettingBinding>(R.layout.activity_setting)) {
            val dividerItemDecoration = DividerItemDecoration(this@SettingActivity, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(getDrawable(R.drawable.shape_divider)!!)
            rvSetting.addItemDecoration(dividerItemDecoration)
            layoutManager = LinearLayoutManager(this@SettingActivity)
            adapter = mAdapter
        }
        mViewModel.settings.observe(this, Observer {
            mAdapter.items = it
            mAdapter.notifyDataSetChanged()
        })
        mViewModel.needCheckTheme.observe(this, Observer {
            if (it) {
                setResult(Activity.RESULT_OK)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
        })
        mViewModel.initSetting()
    }

    override fun onPause() {
        mViewModel.save()
        super.onPause()
    }
}