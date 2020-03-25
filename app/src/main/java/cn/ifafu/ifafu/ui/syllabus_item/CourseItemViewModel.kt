package cn.ifafu.ifafu.ui.syllabus_item

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.RepositoryImpl
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.woolsen.easymvvm.livedata.LiveEvent

class CourseItemViewModel(application: Application) : BaseViewModel(application) {

    val course = MutableLiveData<Course>()
    val setting = MutableLiveData<SyllabusSetting>()
    val editMode = MutableLiveData<Boolean>()
    val resultCode = MutableLiveData<Int>()
    val finishActivity = LiveEvent()

    private var isNewCourse = false

    fun init(id: Long) {
        safeLaunchWithMessage {
            setting.postValue(RepositoryImpl.syllabus.getSetting())
            // 获取跳转课程id
            if (id != -1L) {
                isNewCourse = false
                editMode.postValue(false)
                val c = RepositoryImpl.syllabus.get(id)!!
                course.postValue(c)
            } else {
                isNewCourse = true
                editMode.postValue(true)
                course.postValue(Course())
            }
        }
    }

    fun delete() {
        safeLaunchWithMessage {
            course.value?.run {
                RepositoryImpl.syllabus.delete(this)
                toast("删除成功")
                //用于刷新课表界面
                resultCode.postValue(Activity.RESULT_OK)
                //删除成功，关闭页面
                finishActivity.call()
            }
        }
    }

    fun save(course: Course) {
        safeLaunchWithMessage {
            when {
                course.name.isEmpty() -> {
                    toast(R.string.input_course_name)
                }
                course.nodeCnt <= 0 -> {
                    toast(R.string.select_course_time)
                }
                course.weekSet.isEmpty() -> {
                    toast(R.string.select_course_week)
                }
                else -> {
                    if (isNewCourse) {
                        course.id = this.hashCode().toLong()
                        course.account = RepositoryImpl.user.getInUseAccount()
                        course.local = true
                    }
                    RepositoryImpl.syllabus.save(course)
                    //取消编辑模式
                    editMode.postValue(false)
                    //用于刷新课表界面
                    resultCode.postValue(Activity.RESULT_OK)
                    if (isNewCourse) {
                        finishActivity.call()
                    } else {
                        //更新界面
                        this@CourseItemViewModel.course.postValue(course)
                    }
                    toast(R.string.save_successful)
                }
            }
        }
    }

}
