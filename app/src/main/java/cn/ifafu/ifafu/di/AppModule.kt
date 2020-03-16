package cn.ifafu.ifafu.di

import cn.ifafu.ifafu.data.repository.ParamsRepository
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.repository.impl.ParamsRepositoryImpl
import cn.ifafu.ifafu.ui.main.oldTheme.MainOldThemeViewModel
import dagger.Module
import dagger.Provides
import org.koin.dsl.module
import javax.inject.Singleton

val appModule = module {

    single { Repository }

    factory { MainOldThemeViewModel(get()) }
}