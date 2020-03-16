package cn.ifafu.ifafu.ui.syllabus

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.view.syllabus.CourseBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SyllabusViewModel(application: Application) : BaseViewModel(application) {

    val setting by lazy { MutableLiveData<SyllabusSetting>() }
    val courses by lazy { MutableLiveData<List<List<CourseBase>?>>() }

    fun initData() {
        GlobalScope.launch {
            updateSyllabusSetting().join()
            updateSyllabusLocal()
        }
    }

    /**
     * 从教务管理系统拉取课表
     */
    fun updateSyllabusNet(): Job {
        return safeLaunch(block = {
            event.showDialog()
            val response = ensureLoginStatus {
                Repository.syllabus.fetchAll()
            }
            if (!response.isSuccess || response.data == null) {
                event.showMessage(response.message)
                return@safeLaunch
            }
            response.data.run {
                if (isNullOrEmpty()) {
                    val afterTiao = Repository.syllabus.holidayChange(this)
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
    fun updateSyllabusLocal(): Job {
        return safeLaunch(block = {
            var courses = Repository.syllabus.getAll()
            //若数据库为空则获取网络数据
            if (courses.isEmpty()) {
                val response = Repository.syllabus.fetchAll()
                if (response.isSuccess && response.data != null) {
                    courses = response.data
                } else {
                    throw Exception(response.message)
                }
            }
            //节假日调课，将课程转化为界面现实格式并按周拆分
            val afterTiao = Repository.syllabus.holidayChange(courses)
            this@SyllabusViewModel.courses.postValue(afterTiao)
        }, error = {
            this@SyllabusViewModel.courses.postValue(emptyList())
            event.showMessage(it.errorMessage())
        })
    }

    fun updateSyllabusSetting(): Job {
        return safeLaunchWithMessage {
            val setting = Repository.syllabus.getSetting()
            if (setting.openingDay.startsWith("2019")) {
                setting.openingDay = "2020-03-15"
                Repository.syllabus.saveSetting(setting)
            }
            this@SyllabusViewModel.setting.postValue(setting)
            setting.openingDay = Repository.syllabus.getOpeningDay()
            Repository.syllabus.saveSetting(setting)
        }
    }


}