package cn.ifafu.ifafu.experiment.di

import android.content.Intent
import cn.ifafu.ifafu.data.db.AppDatabase
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.data.NeedLoginCallback
import cn.ifafu.ifafu.experiment.data.UserManager
import cn.ifafu.ifafu.experiment.data.repository.ExamRepository
import cn.ifafu.ifafu.experiment.data.repository.ScoreRepository
import cn.ifafu.ifafu.experiment.data.repository.SemesterRepository
import cn.ifafu.ifafu.experiment.data.repository.UserRepository
import cn.ifafu.ifafu.experiment.data.service.ZFService
import cn.ifafu.ifafu.experiment.data.service.ZFServiceBB
import cn.ifafu.ifafu.experiment.ui.login.LoginActivity
import cn.ifafu.ifafu.experiment.ui.login.LoginViewModel
import cn.ifafu.ifafu.experiment.ui.score_detail.ScoreDetailViewModel
import cn.ifafu.ifafu.experiment.ui.score_filter.ScoreFilterViewModel
import cn.ifafu.ifafu.experiment.ui.score_list.ScoreListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.getInstance(androidContext()) }

    single { get<AppDatabase>().userDaoExp }
    single { get<AppDatabase>().scoreDao }

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


    factory { ScoreRepository(get(), get(), get()) }
    factory { ExamRepository(get(), get(), get()) }

    viewModel { LoginViewModel(get()) }

    scope(named("score")) {
        scoped { ScoreRepository(get(), get(), get()) }
    }

    viewModel {
        val scope = getKoin().getOrCreateScope("score", named("score"))
        val repository = scope.getScope("score").get<ScoreRepository>()
        ScoreListViewModel(repository)
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