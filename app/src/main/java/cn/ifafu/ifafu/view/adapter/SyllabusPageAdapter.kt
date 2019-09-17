package cn.ifafu.ifafu.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.view.syllabus.CourseBase
import cn.ifafu.ifafu.view.syllabus.SyllabusView2
import java.text.SimpleDateFormat
import java.util.*

class SyllabusPageAdapter(var setting: SyllabusSetting,
                          courses: MutableList<MutableList<CourseBase>?>,
                          var onCourseClick: (course: CourseBase) -> Unit)
    : RecyclerView.Adapter<SyllabusPageAdapter.SyllabusViewHolder>() {

    private val mCurrentWeek by lazy {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        DateUtils.getCurrentWeek(format.parse(setting.openingDay), setting.firstDayOfWeek)
    }
    private val mCurrentWeekday by lazy {
        DateUtils.getCurrentWeekday()
    }

    var courses : MutableList<MutableList<CourseBase>?> = courses
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllabusViewHolder {
        val context = parent.context
        val syllabus = SyllabusView2(context)
        syllabus.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        syllabus.rowCount = setting.nodeCnt
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
        val dates = DateUtils.getWeekDates(setting.openingDay, position, setting.firstDayOfWeek, "MM-dd")
        holder.setCourseData(courses.getOrNull(position))
                .setCornerText(dates[0].substring(if (dates[0][0] == '0') 1 else 0, 2) + "\n月")
//                .setToday(if (position + 1 == mCurrentWeek) mCurrentWeekday else -1 )
                .setBeginTimeTexts(setting.beginTimeText)
                .setDateTexts(dates)
    }

    override fun getItemCount(): Int {
        return setting.weekCnt
    }

    inner class SyllabusViewHolder(itemView: SyllabusView2) : RecyclerView.ViewHolder(itemView) {

        private val syllabus: SyllabusView2 = itemView

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

        fun setBeginTimeTexts(beginTimeTexts: Array<String?>?): SyllabusViewHolder {
            syllabus.setBeginTimeTexts(beginTimeTexts)
            return this
        }

        fun setDateTexts(dateTexts: Array<String?>?): SyllabusViewHolder {
            syllabus.dateTexts = dateTexts
            return this
        }
    }

}