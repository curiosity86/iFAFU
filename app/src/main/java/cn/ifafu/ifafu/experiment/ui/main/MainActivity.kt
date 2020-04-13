package cn.ifafu.ifafu.experiment.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseActivity
import cn.ifafu.ifafu.data.entity.GlobalSetting
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val mViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        mViewModel.theme.observe(this, Observer { theme ->
            val navHost = if (theme == GlobalSetting.THEME_OLD) {
                NavHostFragment.create(R.navigation.nav_main_old)
            } else {
                NavHostFragment.create(R.navigation.nav_main_new)
            }
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_main, navHost)
                    .setPrimaryNavigationFragment(navHost)
                    .commit()
        })
    }

}