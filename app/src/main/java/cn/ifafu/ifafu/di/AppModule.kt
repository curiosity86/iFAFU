package cn.ifafu.ifafu.di

import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.ui.main.old_theme.MainOldViewModel
import org.koin.dsl.module

val appModule = module {

    single { RepositoryImpl }

    factory { MainOldViewModel(get()) }
}