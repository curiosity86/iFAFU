package cn.ifafu.ifafu.mvp.syllabus_item

import android.content.Context

import cn.ifafu.ifafu.base.BaseModel
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting

internal class SyllabusItemModel(context: Context) : BaseModel(context), SyllabusItemContract.Model {

    override fun getSyllabusSetting(): SyllabusSetting {
        return repository.syllabusSetting
    }

    override fun save(course: Course) {
        course.account = repository.loginUser.account
        repository.saveCourse(course)
    }

    override fun delete(course: Course) {
        repository.deleteCourse(course)
    }

    override fun getCourseById(id: Long): Course {
        return repository.getCourseById(id)
    }
}
