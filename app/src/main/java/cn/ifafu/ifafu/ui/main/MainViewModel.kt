package cn.ifafu.ifafu.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.data.bean.LiveEvent
import cn.ifafu.ifafu.util.AppUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.security.auth.login.LoginException

class MainViewModel(application: Application) : BaseViewModel(application) {

    val users: LiveData<List<User>> = liveData {
        emit(RepositoryImpl.user.getAll())
    }
    val showMultiUserDialog = MutableLiveData<Boolean>()

    val theme = MutableLiveData<Int>()
    val loading = MutableLiveData<String>()
    val startLoginActivity = LiveEvent()

    private val repo: RepositoryImpl = RepositoryImpl

    /**
     * 初始化所有数据
     */
    fun initActivityData() {
        checkTheme()
    }

    fun switchAccount() {
        safeLaunchWithMessage {
            showMultiUserDialog.postValue(true)
        }
    }

    fun addAccountSuccess() {
        safeLaunchWithMessage {
            val user = RepositoryImpl.user.getInUse()
            initActivityData()
            toast("已切换到${user?.account}")
        }
    }

    fun deleteUser(user: User) = safeLaunchWithMessage {
        RepositoryImpl.user.delete(user.account)
        if (user.account == repo.user.getInUseAccount()) {
            RepositoryImpl.user.getInUse().let {
                if (it == null) {
                    startLoginActivity.call()
                } else {
                    toast("删除成功，已切换到${it.account}")
                    showMultiUserDialog.postValue(false)
                    //重新初始化数据
                    initActivityData()
                }
            }
        } else {
            toast("删除成功")
        }
    }

    fun checkoutTo(user: User) = GlobalScope.launch {
        if (user.account != RepositoryImpl.user.getInUseAccount()) {
            loading.postValue("切换中")
            kotlin.runCatching {
                repo.checkoutTo(user)
                toast("成功切换到${user.account}")
            }.onFailure {
                if (it is LoginException) {
                    startLoginActivity.call()
                } else {
                    toast(it.errorMessage())
                }
            }
            showMultiUserDialog.postValue(false)
            //重新初始化Activity
            initActivityData()
            loading.postValue(null)
        } else {
            toast("正在使用:${user.account}，无需切换")
            showMultiUserDialog.postValue(false)
        }
    }
    fun upgradeApp() = GlobalScope.launch {
        repo.getNewVersion().getOrFailure {
            toast(it.message ?: "Unknown Error")
        }?.let {
            Timber.d("Version: ${it}")
            if (it.versionCode <= AppUtils.getVersionCode(getApplication())) {
                toast("当前为最新版本")
            } else {
                toast("有更新！最新版本为:${it.versionName}\n若未自动更新，请前往ifafu官网手动更新")
            }
        }
    }
    private fun checkTheme() = safeLaunchWithMessage {
        theme.postValue(RepositoryImpl.GlobalSettingRt.get().theme)
    }


}