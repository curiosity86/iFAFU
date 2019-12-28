package cn.ifafu.ifafu.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.mvp.elec_login.ElecLoginViewModel
import cn.ifafu.ifafu.mvp.elective.ElectiveViewModel
import cn.ifafu.ifafu.mvp.score_filter.ScoreFilterViewModel
import cn.ifafu.ifafu.mvp.score_item.ScoreItemViewModel
import cn.ifafu.ifafu.mvp.score_list.ScoreListViewModel
import cn.ifafu.ifafu.mvp.setting.SettingViewModel

object ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    private lateinit var mApplication: Application
    private lateinit var mRepository: Repository

    fun init(application: Application) {
        Repository.init(application)
        mApplication = application
        mRepository = Repository
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ScoreItemViewModel::class.java) -> ScoreItemViewModel(mRepository) as T
            modelClass.isAssignableFrom(ScoreFilterViewModel::class.java) -> ScoreFilterViewModel(mRepository) as T
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> SettingViewModel(mRepository) as T
            modelClass.isAssignableFrom(ElecLoginViewModel::class.java) -> ElecLoginViewModel(mRepository) as T
            modelClass.isAssignableFrom(ScoreListViewModel::class.java) -> ScoreListViewModel(mRepository) as T
            modelClass.isAssignableFrom(ElectiveViewModel::class.java) -> ElectiveViewModel(mRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }


}