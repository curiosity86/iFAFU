package cn.ifafu.ifafu.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.ifafu.ifafu.data.repository.RepositoryImpl

@SuppressLint("Registered")
open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {

        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
            private set
    }
}