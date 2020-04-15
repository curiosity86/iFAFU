package cn.ifafu.ifafu.experiment.di

import android.content.Intent
import cn.ifafu.ifafu.data.db.AppDatabase
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.data.NeedLoginCallback
import cn.ifafu.ifafu.experiment.data.UserManager
import cn.ifafu.ifafu.experiment.data.repository.*
import cn.ifafu.ifafu.experiment.data.service.ZFService
import cn.ifafu.ifafu.experiment.data.service.ZFServiceBB
import cn.ifafu.ifafu.experiment.ui.login.LoginActivity
import cn.ifafu.ifafu.experiment.ui.login.LoginViewModel
import cn.ifafu.ifafu.experiment.ui.main.MainViewModel
import cn.ifafu.ifafu.experiment.ui.score_detail.ScoreDetailViewModel
import cn.ifafu.ifafu.experiment.ui.score_filter.ScoreFilterViewModel
import cn.ifafu.ifafu.experiment.ui.score_list.ScoreListViewModel
import cn.ifafu.ifafu.ui.schedule_setting.SyllabusSettingViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getInstance(androidContext()) }

    single { get<AppDatabase>().userDaoExp }
    single { get<AppDatabase>().scoreDao }
    single { get<AppDatabase>().syllabusSettingDao }

    single<ZFService> { ZFServiceBB() }

    single {
        UserManager(get(), get(), object : NeedLoginCallback {
            override fun callback(user: User?) {
                val app = androidApplication()
                val intent = Intent(app, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                app.startActivity(intent)
            }
        })
    }

    factory { SemesterRepository(get()) }
    factory { UserRepository(get(), get()) }
    factory { CourseRepository(get(), get()) }
    factory { SettingRepository(get(), get()) }
    factory { ScoreRepository(get(), get(), get()) }
    factory { ExamRepository(get(), get(), get()) }

    scope(named("score")) {
        scoped { ScoreRepository(get(), get(), get()) }
    }

    viewModel { MainViewModel(get()) }
    viewModel { LoginViewModel(get()) }

    viewModel {
        SyllabusSettingViewModel(get())
    }

    viewModel {
        val scope = getKoin().getOrCreateScope("score", named("score"))
        val repository = scope.getScope("score").get<ScoreRepository>()
        ScoreListViewModel(repository, scope)
    }
    viewModel { (id: Int) ->
        val scope = getKoin().getOrCreateScope("score", named("score"))
        val repository = scope.getScope("score").get<ScoreRepository>()
        ScoreDetailViewModel(id, repository)
    }
    viewModel {
        val scope = getKoin().getOrCreateScope("score", named("score"))
        val repository = scope.getScope("score").get<ScoreRepository>()
        ScoreFilterViewModel(repository)
    }

}