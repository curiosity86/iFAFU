package cn.ifafu.ifafu.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import cn.ifafu.ifafu.ui.electricity.ElectricityViewModel
import cn.ifafu.ifafu.ui.elective.ElectiveViewModel
import cn.ifafu.ifafu.ui.exam_list.ExamListViewModel
import cn.ifafu.ifafu.ui.login.LoginViewModel
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.score_filter.ScoreFilterViewModel
import cn.ifafu.ifafu.ui.score_item.ScoreItemViewModel
import cn.ifafu.ifafu.ui.score_list.ScoreListViewModel
import cn.ifafu.ifafu.ui.setting.SettingViewModel
import cn.ifafu.ifafu.ui.syllabus.SyllabusViewModel
import cn.ifafu.ifafu.ui.syllabus_item.SyllabusItemViewModel
import cn.ifafu.ifafu.ui.web.WebViewModel

class VMProvider(owner: ViewModelStoreOwner): ViewModelProvider(owner, ViewModelFactory) {

    companion object {
        fun init(application: Application) {
            ViewModelFactory.mApplication = application
        }
    }

    private object ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

        internal lateinit var mApplication: Application

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(mApplication) as T
                modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(mApplication) as T

                modelClass.isAssignableFrom(SyllabusViewModel::class.java) -> SyllabusViewModel(mApplication) as T
                modelClass.isAssignableFrom(SyllabusItemViewModel::class.java) -> SyllabusItemViewModel(mApplication) as T

                modelClass.isAssignableFrom(ScoreListViewModel::class.java) -> ScoreListViewModel(mApplication) as T
                modelClass.isAssignableFrom(ScoreItemViewModel::class.java) -> ScoreItemViewModel(mApplication) as T
                modelClass.isAssignableFrom(ScoreFilterViewModel::class.java) -> ScoreFilterViewModel(mApplication) as T

                modelClass.isAssignableFrom(ExamListViewModel::class.java) -> ExamListViewModel(mApplication) as T

                modelClass.isAssignableFrom(SettingViewModel::class.java) -> SettingViewModel(mApplication) as T
                modelClass.isAssignableFrom(ElectricityViewModel::class.java) -> ElectricityViewModel(mApplication) as T
                modelClass.isAssignableFrom(ElectiveViewModel::class.java) -> ElectiveViewModel(mApplication) as T
                modelClass.isAssignableFrom(WebViewModel::class.java) -> WebViewModel(mApplication) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }


    }
}


