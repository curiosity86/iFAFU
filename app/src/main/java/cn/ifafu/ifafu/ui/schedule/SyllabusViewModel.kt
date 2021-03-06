package cn.ifafu.ifafu.ui.schedule

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.data.repository.impl.RepositoryImpl
import cn.ifafu.ifafu.ui.schedule.view.ScheduleItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class SyllabusViewModel(application: Application) : BaseViewModel(application) {

    private val repo = RepositoryImpl

    val setting = MutableLiveData<SyllabusSetting>()
    val courses = MutableLiveData<List<List<ScheduleItem>?>>()

    val loading = MutableLiveData<String>()
    val backgroundUri: LiveData<Uri?> = setting.switchMap { setting ->
        liveData {
            /*
            emit null, 避免因为两次uri相同而背景不改变
            不信你看[ImageView.setImageUri]
            */
            emit(null)
            val img = File(application.getExternalFilesDir(setting.account), "syllabus_bg.jpg")
            if (img.exists()) {
                emit(Uri.fromFile(img))
            }
        }
    }


    fun initData() {
        GlobalScope.launch {
            updateSyllabusSetting()
            updateSyllabusLocal()
            val openingDay = repo.getOpeningDay().getOrNull() ?: return@launch
            val setting = repo.syllabus.getSetting()
            if (setting.openingDay != openingDay) {
                setting.openingDay = openingDay
                repo.syllabus.saveSetting(setting)
                this@SyllabusViewModel.setting.postValue(setting)
            }
        }
    }

    /**
     * 从教务管理系统拉取课表
     */
    fun updateSyllabusNet(): Job {
        return safeLaunch(block = {
            loading.postValue("获取中")
            val response = ensureLoginStatus {
                repo.syllabus.fetchAll()
            }
            if (!response.isSuccess || response.data == null) {
                toast(response.message)
                return@safeLaunch
            }
            response.data.run {
                if (isNullOrEmpty()) {
                    val afterTiao = repo.syllabus.holidayChange(this)
                    this@SyllabusViewModel.courses.postValue(afterTiao)
                }
            }
            toast("获取课表成功")
            loading.postValue(null)
        }, error = {
            loading.postValue(null)
            toast(it.errorMessage())
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
        toast(it.errorMessage())
    })

    fun updateSyllabusSetting() = safeLaunchWithMessage {
        val setting = repo.syllabus.getSetting()
        this@SyllabusViewModel.setting.postValue(setting)
        repo.syllabus.saveSetting(setting)
    }
}