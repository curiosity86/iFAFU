package cn.ifafu.ifafu.mvp.syllabus_item

import cn.ifafu.ifafu.base.i.IModel
import cn.ifafu.ifafu.base.i.IPresenter
import cn.ifafu.ifafu.base.i.IView
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting
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

        fun getCourseById(id: Long): Course
    }

}
