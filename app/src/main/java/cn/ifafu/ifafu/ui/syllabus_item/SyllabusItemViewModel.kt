package cn.ifafu.ifafu.ui.syllabus_item

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.base.BaseViewModel
import cn.ifafu.ifafu.data.repository.Repository
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting

class SyllabusItemViewModel(application: Application) : BaseViewModel(application) {

    val course by lazy { MutableLiveData<Course>() }
    val setting by lazy { MutableLiveData<SyllabusSetting>() }
    val editMode by lazy { MutableLiveData<Boolean>() }
    val resultCode by lazy { MutableLiveData<Int>() }

    private var isNewCourse = false

    fun init(id: Long) {
        safeLaunchWithMessage {
            setting.postValue(Repository.syllabus.getSetting())
            // 获取跳转课程id
            if (id != -1L) {
                isNewCourse = false
                editMode.postValue(false)
                val c = Repository.syllabus.get(id)!!
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
                Repository.syllabus.delete(this)
                event.showMessage("删除成功")
                //用于刷新课表界面
                resultCode.postValue(Activity.RESULT_OK)
                //删除成功，关闭页面
                event.finishIt()
            }
        }
    }

    fun save(course: Course) {
        safeLaunchWithMessage {
            when {
                course.name.isEmpty() -> {
                    event.showMessage(R.string.input_course_name)
                }
                course.nodeCnt <= 0 -> {
                    event.showMessage(R.string.select_course_time)
                }
                course.weekSet.isEmpty() -> {
                    event.showMessage(R.string.select_course_week)
                }
                else -> {
                    if (isNewCourse) {
                        course.id = this.hashCode().toLong()
                        course.account = Repository.user.getInUseAccount()
                        course.local = true
                    }
                    Repository.syllabus.save(course)
                    //取消编辑模式
                    editMode.postValue(false)
                    //用于刷新课表界面
                    resultCode.postValue(Activity.RESULT_OK)
                    if (isNewCourse) {
                        event.finishIt()
                    } else {
                        //更新界面
                        this@SyllabusItemViewModel.course.postValue(course)
                    }
                    event.showMessage(R.string.save_successful)
                }
            }
        }
    }

}
