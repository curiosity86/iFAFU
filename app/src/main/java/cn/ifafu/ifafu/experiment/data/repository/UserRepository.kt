package cn.ifafu.ifafu.experiment.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.experiment.data.db.UserDao
import cn.ifafu.ifafu.experiment.data.service.ZFService
import kotlinx.coroutines.Dispatchers

class UserRepository(
        private val userDao: UserDao,
        private val zfService: ZFService
) {

    fun login(account: String, password: String): LiveData<Resource<User>> {
        return liveData(Dispatchers.IO) {
            emit(Resource.Loading())
            val user = User(account, password)
            val resp = zfService.login(user).apply {
                if (this is IFResponse.Success) {
                    user.lastLoginTime = System.currentTimeMillis()
                    userDao.saveUser(user)
                }
            }.asResource()
            emit(resp)
        }
    }

    fun loadUser(): LiveData<User> {
        return userDao.getUser()
    }

    fun loadAllUser(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }
}