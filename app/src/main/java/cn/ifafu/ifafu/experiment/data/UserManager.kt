package cn.ifafu.ifafu.experiment.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.experiment.data.db.UserDao
import cn.ifafu.ifafu.experiment.data.service.ZFService
import timber.log.Timber

/**
 * 用户管理器
 */
class UserManager(
        private val userDao: UserDao,
        private val zfService: ZFService,
        private val callback: NeedLoginCallback
) {

    /**
     *  可通过LiveData监听[user]实现切换用户自动切换数据
     *
     *  [if]判断是为了避免更新登录时间导致[user]的刷新
     */
    val user = MediatorLiveData<User>().apply {
        addSource(userDao.getUser()) { user ->
            if (user != null && user.account != this.value?.account) {
                this.value = user
            }
        }
    } as LiveData<User>

    fun <Y> userSwitchMap(transform: (User) -> LiveData<Y>): LiveData<Y> {
        return user.switchMap(transform)
    }

    fun <Y> userMap(transform: (User) -> Y): LiveData<Y> {
        return user.map(transform)
    }

    /**
     * 自动重新登录
     *
     * @param block 需重新登录的获取信息执行块
     */
    fun <T> auto(block: () -> IFResponse<T>): IFResponse<T> {
        val resp = block()
        if (resp is IFResponse.NoAuth) {
            Timber.d("LoadScoresResource#UserManager#auto#NoAuth")
            val userValue = user.value ?: return IFResponse.Failure("无登录账号")
            /* 检验重新登录结果
             成功则重新执行获取操作
             否则返回失败结果 */
            when (val loginResp = zfService.login(userValue)) {
                is IFResponse.Success -> {
                    Timber.d("LoadScoresResource#UserManager#auto => Success")
                    val account = userValue.account
                    userDao.updateUserLastLoginTime(account, System.currentTimeMillis())
                    return block()
                }
                is IFResponse.Failure -> {
                    Timber.d("LoadScoresResource#UserManager#auto => Failure")
                    callback.callback(userValue)
                    return loginResp
                }
                is IFResponse.Error -> {
                    Timber.d("LoadScoresResource#UserManager#auto => Error")
                    return loginResp
                }
            }
        }
        Timber.d("LoadScoresResource#UserManager#auto#Simple")
        return resp
    }

}

/**
 * 重新登录信息错误回调
 */
interface NeedLoginCallback {
    fun callback(user: User? = null)
}
