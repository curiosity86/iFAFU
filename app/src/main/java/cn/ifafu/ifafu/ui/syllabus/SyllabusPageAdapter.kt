package cn.ifafu.ifafu.ui.syllabus

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.ui.syllabus.view.CourseItem
import cn.ifafu.ifafu.ui.syllabus.view.CourseLayout
import cn.ifafu.ifafu.util.DateUtils
import kotlin.collections.ArrayList

class SyllabusPageAdapter(
        var onCourseClickListener: CourseLayout.OnCourseClickListener? = null,
        var onCourseLongClickListener: CourseLayout.OnCourseLongClickListener? = null
) : RecyclerView.Adapter<SyllabusPageAdapter.SyllabusViewHolder>() {

    var courses: List<List<CourseItem>?> = ArrayList()
    var setting: SyllabusSetting = SyllabusSetting()
        set(value) {
            if (value != field) {
                settingChanged = true
                field = value
            }
        }
    private var settingChanged = false

    //将View和ViewHolder绑定在一起
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllabusViewHolder {
        val context = parent.context
        val syllabus = CourseLayout(context)
        syllabus.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        return SyllabusViewHolder(syllabus)
    }

    //将数据显示在View上
    override fun onBindViewHolder(holder: SyllabusViewHolder, position: Int) {
        val syllabus = holder.syllabus
        if (settingChanged) {
            syllabus.oneDayNodeCount = setting.totalNode
            syllabus.displayTime = setting.showBeginTimeText
            syllabus.displayHorizontalLine = setting.showHorizontalLine
            syllabus.displayVerticalLine = setting.showVerticalLine
            syllabus.courseTextSize = setting.textSize.toFloat()
            syllabus.otherTextColor = setting.themeColor
            syllabus.timeText = setting.getBeginTimeText().drop(1)
        }
        onCourseClickListener?.let { syllabus.setOnCourseClickListener(it) }
        onCourseLongClickListener?.let { syllabus.setOnCourseLongClickListener(it) }

        val dates = DateUtils.getWeekDates(setting.openingDay, position, setting.firstDayOfWeek, "MM-dd")
        syllabus.month = dates[0].substring(if (dates[0][0] == '0') 1 else 0, 2).toInt()
        syllabus.dateText = dates.toList()
        courses.getOrNull(position)?.let { syllabus.setCourse(it) }
    }

    override fun getItemCount(): Int {
        return setting.weekCnt
    }

    inner class SyllabusViewHolder(itemView: CourseLayout) : RecyclerView.ViewHolder(itemView) {

        val syllabus = itemView

    }

}