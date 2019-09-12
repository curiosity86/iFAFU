package cn.ifafu.ifafu.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.view.syllabus.*
import cn.ifafu.ifafu.view.syllabus.CourseView.OnCourseClickListener
import java.util.*
import kotlin.collections.ArrayList

class SyllabusPageAdapter : RecyclerView.Adapter<SyllabusPageAdapter.SyllabusViewHolder> {

    private lateinit var mContext: Context
    private lateinit var mLayoutInflater: LayoutInflater

    var courses : MutableList<MutableList<CourseBase>?>
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onCourseClickListener: OnCourseClickListener? = null

    var setting: SyllabusSetting
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    constructor(setting: SyllabusSetting) {
        this.setting = setting
        this.courses = ArrayList()
    }

    constructor(courses: MutableList<MutableList<CourseBase>?>) {
        this.setting = SyllabusSetting()
        this.courses = courses
    }

    constructor(courses: MutableList<MutableList<CourseBase>?>, setting: SyllabusSetting) {
        this.courses = courses
        this.setting = setting
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllabusViewHolder {
        this.mContext = parent.context
        this.mLayoutInflater = LayoutInflater.from(mContext)
        return SyllabusViewHolder(mLayoutInflater.inflate(R.layout.fragment_syllabus, parent, false))
    }

    override fun onBindViewHolder(holder: SyllabusViewHolder, position: Int) {
        val dates = DateUtils.getWeekDates(setting.openingDay, position, setting.firstDayOfWeek, "MM-dd")
        holder.setCourseTextSize(setting.textSize)
                .setShowHorizontalDivider(setting.showHorizontalLine)
                .setShowVerticalDivider(setting.showVerticalLine)
                .setRowCount(setting.nodeCnt)
//                .setFirstDayOfWeek(setting.firstDayOfWeek)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setOnCourseClickListener(onCourseClickListener)
                .setCourseData(courses.getOrNull(position))
                .setThemeColor(setting.themeColor)
                .setCornerText(dates[0].substring(if (dates[0][0] == '0') 1 else 0, 2) + "\n月")
                .setDateTexts(dates)
                .setBeginTimeTexts(if (setting.showBeginTimeText) setting.beginTimeText else null)
                .redraw()
    }

    override fun getItemCount(): Int {
        return setting.weekCnt
    }

    inner class SyllabusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseView: CourseView = itemView.findViewById(R.id.view_course)
        private val sideView: SideView = itemView.findViewById(R.id.view_side)
        private val dateView: DateView = itemView.findViewById(R.id.view_date)
        private val cornerView: TextView = itemView.findViewById(R.id.tv_corner)

        private var courseViewChange = false
        private var sideViewChange = false
        private var dateViewChange = false

        fun setCornerText(text: String): SyllabusViewHolder {
            if (text != cornerView.text) {
                cornerView.text = text
            }
            return this
        }

        fun setThemeColor(@ColorInt color: Int): SyllabusViewHolder {
            if (color != sideView.textColor) {
                sideView.textColor = color
                sideViewChange = true
            }
            if (color != dateView.textColor) {
                dateView.textColor = color
                dateViewChange = true
            }
            return this
        }

        fun setCourseTextSize(textSize: Int): SyllabusViewHolder {
            if (courseView.textSize != textSize) {
                courseView.textSize = textSize
                courseViewChange = true
            }
            return this
        }

        fun setCourseData(courses: List<ToCourseBase>?): SyllabusViewHolder {
            if (courseView.courses != courses) {
                courseView.setCourses(courses)
                courseViewChange = true
            }
            return this
        }

        fun setBeginTimeTexts(beginTimeTexts: Array<String?>?): SyllabusViewHolder {
            if (beginTimeTexts == null && sideView.beginTimeTexts != null
                    || beginTimeTexts != null && sideView.beginTimeTexts == null
                    || beginTimeTexts != null && !beginTimeTexts.contentEquals(sideView.beginTimeTexts)) {
                sideView.beginTimeTexts = beginTimeTexts
                sideViewChange = true
            }
            return this
        }

        fun setDateTexts(dateTexts: Array<String?>?): SyllabusViewHolder {
            if (dateTexts == null && dateView.dateTexts != null
                    || dateTexts != null && dateView.dateTexts == null
                    || dateTexts != null && !dateTexts.contentEquals(dateView.dateTexts)) {
                dateView.dateTexts = dateTexts
                dateViewChange = true
            }
            return this
        }

        fun setFirstDayOfWeek(firstDayOfWeek: Int): SyllabusViewHolder {
            if (courseView.firstDayOfWeek != firstDayOfWeek) {
                courseView.firstDayOfWeek = firstDayOfWeek
                dateViewChange = true
            }
            if (dateView.firstDayOfWeek != firstDayOfWeek) {
                dateView.firstDayOfWeek = firstDayOfWeek
                courseViewChange = true
            }
            return this
        }

        fun setRowCount(rowCount: Int): SyllabusViewHolder {
            if (sideView.rowCount != rowCount) {
                sideView.rowCount = rowCount
                sideViewChange = true
            }
            if (courseView.rowCount != rowCount) {
                courseView.rowCount = rowCount
                courseViewChange = true
            }
            return this
        }

        fun setOnCourseClickListener(listener: OnCourseClickListener?): SyllabusViewHolder {
            courseView.onCourseClickListener = listener
            return this
        }

        fun setShowHorizontalDivider(isShow: Boolean): SyllabusViewHolder {
            if (sideView.isShowHorizontalDivider != isShow) {
                sideView.isShowHorizontalDivider = isShow
                sideViewChange = true
            }
            if (courseView.isShowVerticalDivider != isShow) {
                courseView.isShowHorizontalDivider = isShow
                courseViewChange = true
            }
            return this
        }

        fun setShowVerticalDivider(isShow: Boolean): SyllabusViewHolder {
            if (dateView.isShowVerticalDivider != isShow) {
                dateView.isShowVerticalDivider = isShow
                dateViewChange = true
            }
            if (courseView.isShowVerticalDivider != isShow) {
                courseView.isShowVerticalDivider = isShow
                courseViewChange = true
            }
            return this
        }

        fun redraw() {
            if (courseViewChange) {
                courseView.redraw()
                courseViewChange = false
            }
            if (sideViewChange) {
                sideView.redraw()
                sideViewChange = false
            }
            if (dateViewChange) {
                dateView.redraw()
                dateViewChange = false
            }
        }
    }

}