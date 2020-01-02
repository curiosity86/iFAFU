package cn.ifafu.ifafu.mvp.syllabus_item

import cn.ifafu.ifafu.base.mvp.IModel
import cn.ifafu.ifafu.base.mvp.IPresenter
import cn.ifafu.ifafu.base.mvp.IView
import cn.ifafu.ifafu.entity.Course
import cn.ifafu.ifafu.entity.SyllabusSetting
import java.util.*

class SyllabusItemContract {

    interface View : IView {

        fun getWeekData(): TreeSet<Int>

        fun getNameText(): String?

        fun getAddressText(): String?

        fun getTeacherText(): String?

        fun setNameText(name: String)

        fun setAddressText(address: String)

        fun setTeacherText(teacher: String)

        fun setWeekData(weekData: TreeSet<Int>)

        fun setTimeOPVSelect(op1: Int, op2: Int, op3: Int, text: String)

        fun isEditMode(isEditMode: Boolean)

        fun setTimeOPVOptions(op1: List<String>, op2: List<String>, op3: List<String>)
    }

    interface Presenter : IPresenter {
        fun onSave()

        fun onEdit()

        fun onDelete()

        fun onTimeSelect(options1: Int, options2: Int, options3: Int)
    }

    interface Model : IModel {
        fun getSyllabusSetting(): SyllabusSetting

        fun save(course: Course)

        fun delete(course: Course)

        fun getCourseById(id: Long): Course?
    }

}
