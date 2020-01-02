package cn.ifafu.ifafu.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import cn.ifafu.ifafu.mvp.elec_login.ElecLoginViewModel
import cn.ifafu.ifafu.mvp.elec_main.ElecMainViewModel
import cn.ifafu.ifafu.mvp.elective.ElectiveViewModel
import cn.ifafu.ifafu.mvp.login.LoginViewModel
import cn.ifafu.ifafu.mvp.score_filter.ScoreFilterViewModel
import cn.ifafu.ifafu.mvp.score_item.ScoreItemViewModel
import cn.ifafu.ifafu.mvp.score_list.ScoreListViewModel
import cn.ifafu.ifafu.mvp.setting.SettingViewModel

class ViewModelProvider(owner: ViewModelStoreOwner): ViewModelProvider(owner, ViewModelFactory) {

    companion object {
        fun init(application: Application) {
            ViewModelFactory.mApplication = application
        }
    }

    private object ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

        internal lateinit var mApplication: Application

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(ScoreItemViewModel::class.java) -> ScoreItemViewModel(mApplication) as T
                modelClass.isAssignableFrom(ScoreFilterViewModel::class.java) -> ScoreFilterViewModel(mApplication) as T
                modelClass.isAssignableFrom(SettingViewModel::class.java) -> SettingViewModel(mApplication) as T
                modelClass.isAssignableFrom(ElecLoginViewModel::class.java) -> ElecLoginViewModel(mApplication) as T
                modelClass.isAssignableFrom(ElecMainViewModel::class.java) -> ElecMainViewModel(mApplication) as T
                modelClass.isAssignableFrom(ScoreListViewModel::class.java) -> ScoreListViewModel(mApplication) as T
                modelClass.isAssignableFrom(ElectiveViewModel::class.java) -> ElectiveViewModel(mApplication) as T
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(mApplication) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }


    }
}


