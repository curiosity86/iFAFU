package cn.ifafu.ifafu.app

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import cn.ifafu.ifafu.ui.electricity.ElectricityViewModel
import cn.ifafu.ifafu.ui.elective.ElectiveViewModel
import cn.ifafu.ifafu.ui.exam_list.ExamListViewModel
import cn.ifafu.ifafu.ui.feedback.FeedbackViewModel
import cn.ifafu.ifafu.ui.login.LoginViewModel
import cn.ifafu.ifafu.ui.main.MainViewModel
import cn.ifafu.ifafu.ui.main.oldTheme.MainOldThemeViewModel
import cn.ifafu.ifafu.ui.score_filter.ScoreFilterViewModel
import cn.ifafu.ifafu.ui.score_item.ScoreItemViewModel
import cn.ifafu.ifafu.ui.score_list.ScoreListViewModel
import cn.ifafu.ifafu.ui.setting.SettingViewModel
import cn.ifafu.ifafu.ui.syllabus.SyllabusViewModel
import cn.ifafu.ifafu.ui.syllabus_item.SyllabusItemViewModel
import cn.ifafu.ifafu.ui.syllabus_setting.SyllabusSettingViewModel
import cn.ifafu.ifafu.ui.web.WebViewModel

@Suppress("UNCHECKED_CAST")
class VMProvider(owner: ViewModelStoreOwner): ViewModelProvider(owner, ViewModelFactory) {

    companion object {
        fun init(application: Application) {
            ViewModelFactory.mApplication = application
        }
    }

    private object ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

        internal lateinit var mApplication: Application

        override fun <T : ViewModel> create(modelClass: Class<T>): T  = with(modelClass){
            return when {
                isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(mApplication)
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(mApplication)
                isAssignableFrom(SyllabusViewModel::class.java) -> SyllabusViewModel(mApplication) 
                isAssignableFrom(SyllabusItemViewModel::class.java) -> SyllabusItemViewModel(mApplication)
                isAssignableFrom(SyllabusSettingViewModel::class.java) -> SyllabusSettingViewModel(mApplication)
                isAssignableFrom(ScoreListViewModel::class.java) -> ScoreListViewModel(mApplication)
                isAssignableFrom(ScoreItemViewModel::class.java) -> ScoreItemViewModel(mApplication)
                isAssignableFrom(ScoreFilterViewModel::class.java) -> ScoreFilterViewModel(mApplication)
                isAssignableFrom(ExamListViewModel::class.java) -> ExamListViewModel(mApplication)
                isAssignableFrom(SettingViewModel::class.java) -> SettingViewModel(mApplication)
                isAssignableFrom(ElectricityViewModel::class.java) -> ElectricityViewModel(mApplication)
                isAssignableFrom(ElectiveViewModel::class.java) -> ElectiveViewModel(mApplication)
                isAssignableFrom(WebViewModel::class.java) -> WebViewModel(mApplication)
                isAssignableFrom(FeedbackViewModel::class.java) -> FeedbackViewModel(mApplication)
                else -> throw IllegalArgumentException("Unknown ViewModel class: $name")
            } as T
        }


    }
}


