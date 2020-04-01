package cn.ifafu.ifafu.ui.schedule.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.ui.schedule.view.listener.OnItemClickListener
import cn.ifafu.ifafu.ui.schedule.view.listener.OnItemLongClickListener
import cn.ifafu.ifafu.util.DateUtils
import kotlin.collections.ArrayList

class SyllabusPageAdapter(
        var onCourseClickListener: OnItemClickListener? = null,
        var onCourseLongClickListener: OnItemLongClickListener? = null
) : RecyclerView.Adapter<SyllabusPageAdapter.SyllabusViewHolder>() {

    var courses: List<List<ScheduleItem>?> = ArrayList()
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
        val syllabus = Schedule(context)
        syllabus.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        return SyllabusViewHolder(syllabus)
    }

    //将数据显示在View上
    override fun onBindViewHolder(holder: SyllabusViewHolder, position: Int) {
        val schedule = holder.syllabus
        if (settingChanged) {
            val config = schedule.config.apply {
                totalNodeCount = setting.totalNode
                showTime = setting.showBeginTimeText
                showHorizontalLine = setting.showHorizontalLine
                showVerticalLine = setting.showVerticalLine
                itemTextSize = setting.textSize.toFloat()
                otherTextColor = setting.themeColor
                schedule.setTimeText(setting.getBeginTimeText().drop(1).toTypedArray())
            }
            schedule.config = config
        }
        onCourseClickListener?.let { schedule.setItemClickListener(it) }
        onCourseLongClickListener?.let { schedule.setItemLongClickListener(it) }

        val dates = DateUtils.getWeekDates(setting.openingDay, position, setting.firstDayOfWeek, "MM-dd")
        schedule.setMonth(dates[0].substring(if (dates[0][0] == '0') 1 else 0, 2).toInt())
        schedule.setDateTexts(dates)
        courses.getOrNull(position)?.let { schedule.setItems(it) }
    }

    override fun getItemCount(): Int {
        return setting.weekCnt
    }

    inner class SyllabusViewHolder(itemView: Schedule) : RecyclerView.ViewHolder(itemView) {

        val syllabus = itemView

    }

}