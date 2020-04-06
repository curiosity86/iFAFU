package cn.ifafu.ifafu.experiment.di

import cn.ifafu.ifafu.data.db.AppDatabase
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.repository.ScoreRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single { AppDatabase.getInstance(androidContext()) }

    single { get<AppDatabase>().scoreDaoExp }

    single { ScoreRepository(User(), get(), get()) }

}

val mvvmModule = module {
//    viewModel { ScoreFilterViewModel(get()) }
}
