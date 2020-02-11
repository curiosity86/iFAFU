package cn.ifafu.ifafu.ui.syllabus

import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.base.mvvm.BaseViewModel
import cn.ifafu.ifafu.data.Repository
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.view.syllabus.CourseBase

class SyllabusViewModel(application: Application) : BaseViewModel(application) {

    val setting by lazy { MutableLiveData<SyllabusSetting>() }
    val courses by lazy { MutableLiveData<List<List<CourseBase>?>>() }

    fun initData() {
        updateSyllabusLocal()
        updateSyllabusSetting()
    }

    /**
     * 从教务管理系统拉取课表
     */
    fun updateSyllabusNet() {
        safeLaunch(block = {
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
    fun updateSyllabusLocal() {
        safeLaunch(block = {
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

    fun updateSyllabusSetting() {
        safeLaunch {
            setting.postValue(Repository.syllabus.getSetting())
        }
    }


}