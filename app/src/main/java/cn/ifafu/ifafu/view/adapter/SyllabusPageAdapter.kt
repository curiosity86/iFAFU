package cn.ifafu.ifafu.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.view.syllabus.CourseBase
import cn.ifafu.ifafu.view.syllabus.SyllabusView
import java.util.*
import kotlin.collections.ArrayList

class SyllabusPageAdapter(var setting: SyllabusSetting,
                          var courses: List<List<CourseBase>?> = ArrayList(),
                          var onCourseClick: (course: CourseBase) -> Unit)
    : RecyclerView.Adapter<SyllabusPageAdapter.SyllabusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllabusViewHolder {
        val context = parent.context
        val syllabus = SyllabusView(context)
        syllabus.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        syllabus.rowCount = setting.totalNode
        syllabus.isShowTimeTexts = setting.showBeginTimeText
        syllabus.isShowHorizontalDivider = setting.showHorizontalLine
        syllabus.isShowVerticalDivider = setting.showVerticalLine
        syllabus.firstDayOfWeek = Calendar.SUNDAY
        syllabus.courseTextSize = setting.textSize
        syllabus.sideTextColor = setting.themeColor
        syllabus.dateTextColor = setting.themeColor
        syllabus.setOnCourseClickListener { _, course ->
            onCourseClick.invoke(course)
        }
        return SyllabusViewHolder(syllabus)
    }

    override fun onBindViewHolder(holder: SyllabusViewHolder, position: Int) {
//        val c = Calendar.getInstance()
//        c.time = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(setting.openingDay)
//        c.add(Calendar.WEEK_OF_YEAR, position)
//        val monthInCN = "${c[Calendar.MONTH] + 1}\n月"
//        val dates = Array<String?>(7) {""}
//        val format = SimpleDateFormat("MM-dd", Locale.CHINA)
//        for (i in 0 until 7) {
//            dates[i] = format.format(c.time)
//            c.add(Calendar.DAY_OF_YEAR, 1)
//        }
        val dates = DateUtils.getWeekDates(setting.openingDay, position, setting.firstDayOfWeek, "MM-dd")
        holder.setCourseData(courses.getOrNull(position))
//                .setCornerText(monthInCN)
                .setCornerText(dates[0].substring(if (dates[0][0] == '0') 1 else 0, 2) + "\n月")
//                .setToday(if (position + 1 == mCurrentWeek) mCurrentWeekday else -1 )
                .setBeginTimeTexts(setting.beginTimeText.copyOfRange(1, setting.beginTimeText.size))
                .setDateTexts(dates)
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

        fun setBeginTimeTexts(beginTimeTexts: Array<String>): SyllabusViewHolder {
            syllabus.setBeginTimeTexts(beginTimeTexts)
            return this
        }

        fun setDateTexts(dateTexts: Array<String?>?): SyllabusViewHolder {
            syllabus.dateTexts = dateTexts
            return this
        }
    }

}