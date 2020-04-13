package cn.ifafu.ifafu.experiment.ui.main.old

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import cn.ifafu.ifafu.constant.getMessage
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import cn.ifafu.ifafu.ui.main.bean.ExamPreview
import cn.ifafu.ifafu.ui.main.bean.ScorePreview
import cn.ifafu.ifafu.ui.main.bean.Weather
import cn.ifafu.ifafu.util.AppUtils
import com.tencent.bugly.beta.tinker.TinkerManager.getApplication
import kotlinx.coroutines.*
import javax.security.auth.login.LoginException

class MainOldViewModel(val repo: RepositoryImpl) : ViewModel() {

    private val coroutineScope: CoroutineScope = GlobalScope

    val user: MutableLiveData<User> = MutableLiveData()
    val semester: LiveData<String> = liveData {
        val semester = RepositoryImpl.getNowSemester().toTitle()
        emit(semester)
    }
    val online = MutableLiveData<Boolean>()
    val weather = MutableLiveData<Weather>()
    val classPreview = MutableLiveData<ClassPreview>()
    val examsPreview = MutableLiveData<ExamPreview>()
    val scorePreview = MutableLiveData<ScorePreview>()

    // 多用户管理 value=null: 取消Dialog
    val users: MutableLiveData<List<User>> = MutableLiveData()
    val startLoginActivity: MutableLiveData<Unit> = MutableLiveData()
    val finish: MutableLiveData<Unit> = MutableLiveData()

    val message: MutableLiveData<String> = MutableLiveData()
    val loading: MutableLiveData<String> = MutableLiveData()

    init {
        GlobalScope.launch(Dispatchers.IO) {
            user.postValue(RepositoryImpl.user.getInUse())
            online.postValue(true)
        }
    }

    fun updateClassPreview() = GlobalScope.launch(Dispatchers.IO) {
        val courses = repo.syllabus.getAll()
        val setting = repo.syllabus.getSetting()
        //调课信息
        val holidayFromToMap = repo.syllabus.getAdjustmentInfo()
        classPreview.postValue(ClassPreview.convert(courses, holidayFromToMap, setting))
    }

    fun updateExamsPreview() = GlobalScope.launch(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val semester = RepositoryImpl.getNowSemester()
        val exams = RepositoryImpl.exam.getAll(semester.yearStr, semester.termStr)
                .filter { it.startTime > now || it.startTime == 0L }
        examsPreview.postValue(ExamPreview.convert(exams))
    }

    fun updateWeather() = GlobalScope.launch {
        repo.getWeather("101230101").getOrNull()?.let {
            weather.postValue(it)
        }
    }

    fun updateScorePreview() = GlobalScope.launch(Dispatchers.IO) {
//        kotlin.runCatching {
//            val db = RepositoryImpl.ScoreRt.getNow()
//            scorePreview.postValue(ScorePreview.convert(db))
//            val scores = RepositoryImpl.ScoreRt.fetchNow().data
//            scorePreview.postValue(ScorePreview.convert(scores))
//        }
    }

    fun switchAccount() = coroutineScope.launch {
        val users = repo.user.getAll()
        this@MainOldViewModel.users.postValue(users)
    }

    fun addAccountSuccessful() = coroutineScope.launch {
        val user = RepositoryImpl.user.getInUse()
        this@MainOldViewModel.user.postValue(user)
        message.postValue("已切换到${user?.account}")
    }

    fun deleteUser(user: User) = coroutineScope.launch {
        val loginUser = repo.user.getInUse()
        RepositoryImpl.user.delete(user.account)
        if (user.account == loginUser?.account) {
            //若删除的为当前使用的账号
            RepositoryImpl.user.getInUse().let {
                users.postValue(null)
                if (it == null) {
                    //若数据库中不存在用户信息，则跳转至登录界面
                    startLoginActivity.postValue(Unit)
                    finish.postValue(Unit)
                } else {
                    //重新初始化数据
                    loading.postValue("切换中")
                    this@MainOldViewModel.user.postValue(it)
                    initPreviewData().joinAll()
                    message.postValue("删除成功，已切换到${it.account}")
                    loading.postValue(null)
                }
            }
        } else {
            message.postValue("删除成功")
        }
    }

    fun checkoutTo(user: User) = GlobalScope.launch {
        if (user.account != RepositoryImpl.user.getInUseAccount()) {
            loading.postValue("切换中")
            kotlin.runCatching {
                repo.checkoutTo(user)
                this@MainOldViewModel.user.postValue(user)
                //重新初始化Activity
                initPreviewData().joinAll()
                message.postValue("成功切换到${user.account}")
            }.onFailure {
                if (it is LoginException) {
                    startLoginActivity.postValue(Unit)
                } else {
                    message.postValue(it.getMessage())
                }
            }
            users.postValue(null)
            loading.postValue(null)
        } else {
            message.postValue("正在使用:${user.account}，无需切换")
            users.postValue(null)
        }
    }

    fun upgradeApp() = GlobalScope.launch {
        repo.getNewVersion().getOrFailure {
            message.postValue(it.getMessage())
        }?.let {
            if (it.versionCode <= AppUtils.getVersionCode(getApplication())) {
                message.postValue("当前为最新版本")
            } else {
                message.postValue("有更新！最新版本为:${it.versionName}\n若未自动更新，请前往ifafu官网手动更新")
            }
        }
    }

    private fun initPreviewData() =
            listOf(
                    updateClassPreview(),
                    updateExamsPreview(),
                    updateScorePreview()
            )
}