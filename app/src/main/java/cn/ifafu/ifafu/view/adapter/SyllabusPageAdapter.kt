package cn.ifafu.ifafu.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.view.syllabus.CourseBase
import cn.ifafu.ifafu.view.syllabus.SyllabusView
import java.util.*
import kotlin.collections.ArrayList

class SyllabusPageAdapter(var onCourseClick: ((course: CourseBase) -> Unit)? = null)
    : RecyclerView.Adapter<SyllabusPageAdapter.SyllabusViewHolder>() {

    var courses: List<List<CourseBase>?> = ArrayList()
    var setting: SyllabusSetting = SyllabusSetting()

    //将View和ViewHolder绑定在一起
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllabusViewHolder {
        val context = parent.context
        val syllabus = SyllabusView(context)
        syllabus.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        return SyllabusViewHolder(syllabus)
    }

    //将数据显示在View上
    override fun onBindViewHolder(holder: SyllabusViewHolder, position: Int) {
        val syllabus = holder.itemView as SyllabusView
        syllabus.rowCount = setting.totalNode
        syllabus.isShowTimeTexts = setting.showBeginTimeText
        syllabus.isShowHorizontalDivider = setting.showHorizontalLine
        syllabus.isShowVerticalDivider = setting.showVerticalLine
        syllabus.firstDayOfWeek = Calendar.SUNDAY
        syllabus.courseTextSize = setting.textSize
        syllabus.sideTextColor = setting.themeColor
        syllabus.dateTextColor = setting.themeColor
        syllabus.setOnCourseClickListener { _, course ->
            onCourseClick?.invoke(course)
        }
        val dates = DateUtils.getWeekDates(setting.openingDay, position, setting.firstDayOfWeek, "MM-dd")
        holder.setCourseData(courses.getOrNull(position))
//                .setCornerText(monthInCN)
                .setCornerText(dates[0].substring(if (dates[0][0] == '0') 1 else 0, 2) + "\n月")
//                .setToday(if (position + 1 == mCurrentWeek) mCurrentWeekday else -1 )
                .setDateTexts(dates)
                .setBeginTimeTexts(setting.getBeginTimeText())
    }

    override fun getItemCount(): Int {
        return setting.weekCnt
    }

    inner class SyllabusViewHolder(itemView: SyllabusView) : RecyclerView.ViewHolder(itemView) {

        private val syllabus: SyllabusView = itemView

        fun setCornerText(text: String): SyllabusViewHolder {
            syllabus.setCornerText(text)
            return this
        }

//        fun setToday(weekday: Int): SyllabusViewHolder {
//            syllabus.setToday(weekday)
//            return this
//        }

        fun setCourseData(courses: List<CourseBase>?): SyllabusViewHolder {
            syllabus.replaceCourseData(courses)
            return this
        }

        fun setBeginTimeTexts(beginTimeTexts: List<String>): SyllabusViewHolder {
            syllabus.setBeginTimeTexts(beginTimeTexts)
            return this
        }

        fun setDateTexts(dateTexts: Array<String?>?): SyllabusViewHolder {
            syllabus.dateTexts = dateTexts
            return this
        }
    }

}