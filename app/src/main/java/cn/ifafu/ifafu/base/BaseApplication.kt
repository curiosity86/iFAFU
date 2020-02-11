package cn.ifafu.ifafu.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.ifafu.ifafu.app.VMProvider
import cn.ifafu.ifafu.data.Repository

@SuppressLint("Registered")
open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        VMProvider.init(this)
        Repository.init(this)
        appContext = applicationContext
    }

    companion object {

        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        lateinit var appContext: Context
            private set
    }
}