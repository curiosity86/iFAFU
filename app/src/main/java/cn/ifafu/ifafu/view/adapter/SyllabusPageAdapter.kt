package cn.ifafu.ifafu.view.adapter

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cn.ifafu.ifafu.data.entity.Course
import cn.ifafu.ifafu.data.entity.SyllabusSetting
import cn.ifafu.ifafu.util.DateUtils
import cn.ifafu.ifafu.view.syllabus.CourseView.OnCourseClickListener
import cn.ifafu.ifafu.view.syllabus.SyllabusView
import java.util.*

class SyllabusPageAdapter(
        private val context: Context,
        private var setting: SyllabusSetting
) : Adapter<SyllabusPageAdapter.VH>() {

    private val mCourseList = SparseArray<MutableList<Course>>()
    private var onCourseClickListener: OnCourseClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view: View = SyllabusView(context)
        val lp = LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        view.layoutParams = lp
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val view = holder.syllabusView

        view.setCourseTextSize(setting.textSize)
        view.setRowCount(setting.nodeCnt)
        view.setBeginTimeTexts(setting.beginTimeText)
        view.setFirstDayOfWeek(setting.firstDayOfWeek)

        view.setOnCourseClickListener(onCourseClickListener)
        view.setCourseData(mCourseList.get(position))
        view.setDateTexts(DateUtils.getWeekDates(
                setting.openingDay, position, setting.firstDayOfWeek, "MM-dd"))
        view.redraw()
    }

    override fun getItemCount(): Int {
        return setting.weekCnt
    }

    fun setSyllabusSetting(setting: SyllabusSetting) {
        this.setting = setting
    }

    /**
     * 修改后需调用[notifyDataSetChanged]，才会更新布局
     *
     * @param courseList courses
     */
    fun setCourseList(courseList: List<Course>) {
        mCourseList.clear()
        for (course in courseList) {
            for (week in course.weekSet) {
                addCourse(week - 1, course)
            }
        }
    }

    private fun addCourse(index: Int, course: Course) {
        if (mCourseList.get(index) == null) {
            mCourseList.put(index, ArrayList())
        }
        mCourseList.get(index).add(course)
    }

    fun setCourserClickListener(listener: OnCourseClickListener?) {
        onCourseClickListener = listener
    }

    inner class VH(syllabusView: View) : ViewHolder(syllabusView) {
        var syllabusView: SyllabusView = syllabusView as SyllabusView
    }

}