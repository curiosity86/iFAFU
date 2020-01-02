package cn.ifafu.ifafu.mvp.syllabus_item

import android.content.Context

import cn.ifafu.ifafu.base.mvp.BaseModel
import cn.ifafu.ifafu.entity.Course
import cn.ifafu.ifafu.entity.SyllabusSetting

internal class SyllabusItemModel(context: Context) : BaseModel(context), SyllabusItemContract.Model {

    override fun getSyllabusSetting(): SyllabusSetting {
        return mRepository.getSyllabusSetting()
    }

    override fun save(course: Course) {
        course.account = mRepository.getInUseUser()!!.account
        mRepository.saveCourse(course)
    }

    override fun delete(course: Course) {
        mRepository.deleteCourse(course)
    }

    override fun getCourseById(id: Long): Course? {
        return mRepository.getCourseById(id)
    }
}
