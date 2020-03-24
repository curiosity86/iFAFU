package cn.ifafu.ifafu.ui.syllabus

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.data.new_http.NetSourceImpl
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.ui.syllabus.view.CourseItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class SyllabusViewModel(application: Application) : BaseViewModel(application) {

    private val repo = RepositoryImpl

    val setting by lazy { MutableLiveData<SyllabusSetting>() }
    val courses by lazy { MutableLiveData<List<List<CourseItem>?>>() }

    fun initData() {
        GlobalScope.launch {
            updateSyllabusSetting().join()
            updateSyllabusLocal()
            val openingDay = NetSourceImpl().getOpeningDay().getOrNull() ?: return@launch
            val setting = repo.syllabus.getSetting()
            if (setting.openingDay != openingDay) {
                Timber.d("update opening day: ${openingDay}, before: ${setting.openingDay}")
                setting.openingDay = openingDay
                this@SyllabusViewModel.setting.postValue(setting)
                repo.syllabus.saveSetting(setting)
                updateSyllabusLocal()
            }
        }
    }

    /**
     * 从教务管理系统拉取课表
     */
    fun updateSyllabusNet(): Job {
        return safeLaunch(block = {
            event.showDialog()
            val response = ensureLoginStatus {
                repo.syllabus.fetchAll()
            }
            if (!response.isSuccess || response.data == null) {
                event.showMessage(response.message)
                return@safeLaunch
            }
            response.data.run {
                if (isNullOrEmpty()) {
                    val afterTiao = repo.syllabus.holidayChange(this)
                    this@SyllabusViewModel.courses.postValue(afterTiao)
                }
            }
            event.showMessage("获取课表成功")
            event.hideDialog()
        }, error = {
            event.hideDialog()
            event.showMessage(it.errorMessage())
        })
    }

    /**
     * 通过数据库课表数据更新界面
     * 用于增删改课程与修改设置后的界面刷新
     */
    fun updateSyllabusLocal(): Job = safeLaunch(block = {
        var courses = RepositoryImpl.syllabus.getAll()
        //若数据库为空则获取网络数据，并更新课表设置
        if (courses.isEmpty()) {
            val response = repo.syllabus.fetchAll()
            if (response.isSuccess && response.data != null) {
                courses = response.data
            } else {
                throw Exception(response.message)
            }
        }
        //节假日调课，将课程转化为界面现实格式并按周拆分
        val afterTiao = RepositoryImpl.syllabus.holidayChange(courses)
        this@SyllabusViewModel.courses.postValue(afterTiao)
    }, error = {
        this@SyllabusViewModel.courses.postValue(emptyList())
        event.showMessage(it.errorMessage())
    })

    fun updateSyllabusSetting() = safeLaunchWithMessage {
        val setting = RepositoryImpl.syllabus.getSetting()
        Timber.d("Update Syllabus Setting")
        this@SyllabusViewModel.setting.postValue(setting)
        repo.syllabus.saveSetting(setting)
    }
}