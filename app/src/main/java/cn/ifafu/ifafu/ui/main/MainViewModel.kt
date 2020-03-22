package cn.ifafu.ifafu.ui.main

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.Constant
import cn.ifafu.ifafu.app.IFAFU
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.bean.Weather
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.data.entity.GlobalSetting
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.ui.main.bean.ClassPreview
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.view.timeline.TimeAxis
import com.alibaba.fastjson.JSONObject
import com.tencent.bugly.beta.Beta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : BaseViewModel(application) {

    val users by lazy { MutableLiveData<List<User>>() }
    val theme by lazy { MutableLiveData<Int>() }
    val isShowSwitchAccountDialog by lazy { MutableLiveData<Boolean>() }

    val nextCourse by lazy { MutableLiveData<ClassPreview>() }
    val weather by lazy { MutableLiveData<Weather>() }
    val inUseUser by lazy { MutableLiveData<User>() }

    /**
     * 新版主题
     */
    val timeAxis by lazy { MutableLiveData<List<TimeAxis>>() }
    val schoolIcon by lazy { MutableLiveData<Drawable>() }

    private val repo: Repository = Repository

    /**
     * 初始化所有数据
     */
    fun initActivityData() {
        checkTheme()
    }

    fun initFragmentData() {
        safeLaunchWithMessage {
            when (theme.value) {
                GlobalSetting.THEME_NEW -> {
                    initNewTabMenu()
                }
            }
            val user = Repository.user.getInUse()
            withContext(Dispatchers.Main) {
                inUseUser.value = user
            }
            if (user == null) return@safeLaunchWithMessage
            inUseUser.postValue(user)
        }
    }

    private fun initNewTabMenu() {
        safeLaunchWithMessage {
            schoolIcon.postValue(when (inUseUser.value?.school) {
                Constant.FAFU -> getApplication<Application>().getDrawable((R.drawable.fafu_bb_icon_white))
                Constant.FAFU_JS -> getApplication<Application>().getDrawable((R.drawable.fafu_js_icon_white))
                else -> getApplication<Application>().getDrawable((R.mipmap.ic_launcher_round))
            })
        }
    }

    fun updateNextCourse() {
        safeLaunchWithMessage {
            nextCourse.postValue(getNextCourse())
        }
    }

    fun updateTimeAxis() {
        safeLaunchWithMessage {
            val list = ArrayList<TimeAxis>()
            val now = Date()
            val holidays = Repository.syllabus.getHoliday()
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            for (holiday in holidays) {
                val date = format.parse(holiday.date)
                val day = DateUtils.calcLastDays(now, date)
                if (day >= 0) {
                    val axis = TimeAxis(
                            holiday.name, holiday.date, day)
                    list.add(axis)
                }
            }
            val exams = Repository.exam.getNow()
            val toTimeAxis: (List<Exam>) -> List<TimeAxis> = {
                val timeAxises = ArrayList<TimeAxis>()
                for (exam in it) {
                    if (exam.startTime == 0L) { //暂无时间信息
                        continue
                    }
                    val date = Date(exam.startTime)
                    val day = DateUtils.calcLastDays(now, date)
                    if (day >= 0) {
                        val axis = TimeAxis(
                                exam.name, format.format(Date(exam.startTime)), day)
                        timeAxises.add(axis)
                    }
                }
                timeAxises
            }
            list.addAll(toTimeAxis(exams))
            list.sortWith(Comparator { o1, o2 -> o1.day.compareTo(o2.day) })
            timeAxis.postValue(list)
            Repository.exam.fetchNow().data?.run {
                list.addAll(toTimeAxis(this))
                timeAxis.postValue(list)
            }
        }
    }

    private suspend fun getNextCourse(): ClassPreview {
        val courses = this.repo.syllabus.getAll()
        val setting = this.repo.syllabus.getSetting()
        //调课信息
        val holidayFromToMap = this.repo.syllabus.getAdjustmentInfo()
        return ClassPreview.convert(courses, holidayFromToMap, setting)
    }

    fun updateWeather() {
        safeLaunchWithMessage {
            Repository.WeatherRt.fetch("101230101").data?.run {
                this@MainViewModel.weather.postValue(this)
            }
        }
    }

    fun importAccount() {
        GlobalScope.launch {
            try {
                val cm = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val data = cm.primaryClip
                val item = data!!.getItemAt(0)
                val content = item.text.toString()
                val list = JSONObject.parseArray(content, User::class.java)
                list.forEach {
                    Repository.user.save(it)
                }
                event.showMessage("导入成功")
            } catch (e: Exception) {
                event.showMessage("导入失败")
            }
        }
    }

    fun checkTheme() {
        safeLaunchWithMessage {
            theme.postValue(Repository.GlobalSettingRt.get().theme)
        }
    }

    fun upgradeApp() {
        safeLaunchWithMessage {
            val upgradeInfo = Beta.getUpgradeInfo()
            if (upgradeInfo != null && upgradeInfo.versionCode > GlobalLib.getLocalVersionCode(getApplication())) {
                Beta.checkUpgrade()
            } else {
                event.showMessage(R.string.is_last_version)
            }
        }
    }

    fun switchAccount() {
        safeLaunchWithMessage {
            users.postValue(Repository.user.getAll())
            isShowSwitchAccountDialog.postValue(true)
        }
    }

    fun addAccountSuccess() {
        safeLaunchWithMessage {
            val user = Repository.user.getInUse()
            initActivityData()
            event.showMessage("已切换到${user?.account}")
        }
    }

    fun deleteUser(user: User) {
        safeLaunchWithMessage {
            Repository.user.delete(user.account)
            if (user.account == inUseUser.value?.account) {
                Repository.user.getInUse().run {
                    if (this@run == null) {
                        event.startLoginActivity()
                    } else {
                        event.showMessage("删除成功，已切换到${account}")
                        isShowSwitchAccountDialog.postValue(false)
                        //重新初始化数据
                        initActivityData()
                    }
                }
            } else {
                event.showMessage("删除成功")
            }
        }
    }

    fun checkoutTo(user: User) {
        safeLaunchWithMessage {
            if (user.account != Repository.user.getInUseAccount()) {
                event.showDialog()
                Repository.user.saveLoginOnly(user)
                val job = safeLaunchWithMessage {
                    Repository.user.login(user)
                }
                IFAFU.loginJob = job
                job.join()
                event.showMessage("成功切换到${user.account}")
                isShowSwitchAccountDialog.postValue(false)
                //重新初始化Activity
                initActivityData()
                event.hideDialog()
            }
        }
    }
}