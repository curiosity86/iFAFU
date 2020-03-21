package cn.ifafu.ifafu.di

import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.ui.main.old_theme.MainOldViewModel
import org.koin.dsl.module

val appModule = module {

    single { Repository }

    factory { MainOldViewModel(get()) }
}