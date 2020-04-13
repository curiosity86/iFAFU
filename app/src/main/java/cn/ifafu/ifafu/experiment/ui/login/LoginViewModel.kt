package cn.ifafu.ifafu.experiment.ui.login

import androidx.lifecycle.*
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.experiment.bean.Resource
import cn.ifafu.ifafu.experiment.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {


    private val _loginStatus = MediatorLiveData<Resource<User>>()
    val loginStatus: LiveData<Resource<User>> = _loginStatus

    val account = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun login() = viewModelScope.launch {
        val account = account.value ?: ""
        val password = password.value ?: ""
        if (!isAccountValid(account)) {
            _loginStatus.value = Resource.Error("账号格式错误！")
        } else if (!isPasswordValid(password)) {
            _loginStatus.value = Resource.Error("密码格式错误！")
        } else {
            _loginStatus.addSource(userRepository.login(account, password)) {
                _loginStatus.value = it
            }
        }
    }

    private fun isAccountValid(account: String): Boolean {
        return account.length == 9 || account.length == 10
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

}