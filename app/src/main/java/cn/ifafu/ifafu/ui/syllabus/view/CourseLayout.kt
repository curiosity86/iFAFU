package cn.ifafu.ifafu.ui.syllabus.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.util.ColorUtils
import cn.ifafu.ifafu.util.DensityUtils
import java.util.*
import kotlin.collections.HashMap

class CourseLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr) {

    private var onCourseClickListener: OnCourseClickListener? = null
    private var onCourseLongClickListener: OnCourseLongClickListener? = null
    var otherTextColor = Color.BLACK
        set(value) {
            if (value != field) {
                field = value
                updateOtherTextColor()
            }
        }
    var oneDayNodeCount = 12 //每天的上课节数
        set(value) {
            if (value != field) {
                field = value
                updateOneDayNodeCount()
            }
        }
    var displayHorizontalLine = true //是否显示水平分割线
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }
    var displayVerticalLine = true //是否显示垂直分割线
        set(value) {
            if (value != field) {
                field = value
                invalidate()
            }
        }
    var displayDate = true //是否显示日期栏
        set(value) {
            if (value != field) {
                field = value
                isDisplayDateText(value)
            }
        }
    var displayTime = true //是否显示上课时间栏
        set(value) {
            if (value != field) {
                field = value
                isDisplayTimeText(value)
            }
        }
    var courseTextSize = 12F //课程字体大小
        set(value) {
            if (value != field) {
                field = value
                updateCourseTextSize()
            }
        }
    var timeText = emptyList<String>()
        set(value) {
            if (value != field) {
                field = value
                updateTimeText()
            }
        }
    var dateText = emptyList<String>()
        set(value) {
            if (value != field) {
                field = value
                updateDateText()
            }
        }
    var month = 1
        set(value) {
            if (value != field) {
                field = value
                updateMonthText()
            }
        }

    private lateinit var cornerTextView: TextView
    private lateinit var weekLayouts: Array<LinearLayout>
    private lateinit var noteLayouts: Array<RelativeLayout>

    private val dayOfWeekCN = listOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    private val courseItemViewMap: MutableMap<CourseItem, TextView> = HashMap()

    private val colorPool = HashMap<Int, @ColorInt Int>()

    private val dateLayoutHeightWeight = 0.8F
    private val sideLayoutWidthWeight = 0.45F

    private val dp1 = DensityUtils.dp2px(context, 1F)

    init {
        initCornerLayout()
        initNodeLayout()
        initWeekLayout()
    }

    private fun updateOneDayNodeCount() {
        val courses = courseItemViewMap.keys.toList()
        removeAllViews()
        addCourse(courses)
        initCornerLayout()
        initNodeLayout()
        initWeekLayout()
        updateMonthText()
        invalidate()
    }

    fun addCourse(courses: List<CourseItem>) {
        courses.forEach { addCourse(it) }
    }

    fun addCourse(course: CourseItem) {
        val itemView = TextView(context).apply {
            layoutParams = LayoutParams().apply {
                width = 0
                height = 0
                rowSpec = spec(course.startNode, course.nodeCount, 1f)
                columnSpec = spec(course.dayOfWeek, 1, 1f)
            }
            setPadding(dp1, 0, dp1, 0)
            gravity = Gravity.CENTER
            val display = "${course.name}\n@${course.address}"
            setBackgroundColor(getRandomColor(display))
            text = display
            paint.isFakeBoldText = true
            setTextSize(TypedValue.COMPLEX_UNIT_SP, courseTextSize)
            setTextColor(Color.WHITE)
            tag = course
            onCourseClickListener?.run {
                setOnClickListener {
                    this.onClick(course)
                }
            }
            onCourseLongClickListener?.run {
                setOnLongClickListener {
                    this.onLongClick(course)
                    true
                }
            }
        }
        courseItemViewMap[course] = itemView
        addView(itemView)
    }

    fun setOnCourseLongClickListener(listener: OnCourseLongClickListener) {
        if (onCourseLongClickListener == listener) {
            return
        }
        onCourseLongClickListener = listener
        courseItemViewMap.forEach { e ->
            e.value.setOnClickListener {
                listener.onLongClick(e.key)
            }
        }
    }

    fun setOnCourseClickListener(listener: OnCourseClickListener) {
        if (onCourseClickListener == listener) {
            return
        }
        onCourseClickListener = listener
        courseItemViewMap.forEach { e ->
            e.value.setOnClickListener {
                listener.onClick(e.key)
            }
        }
    }

    private fun isDisplayTimeText(display: Boolean) {
        val visibility = if (display) View.VISIBLE else View.GONE
        for (noteLayout in noteLayouts) {
            val timeTextView = noteLayout.findViewById<TextView>(R.id.id_time_textview)
            if (timeTextView != null) {
                timeTextView.visibility = visibility
            }
        }
    }

    private fun updateCourseTextSize() {
        courseItemViewMap.values.forEach {
            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, courseTextSize)
        }
    }

    fun setCourse(courses: List<CourseItem>) {
        removeAllCourse()
        addCourse(courses)
    }
    
    fun removeAllCourse() {
        courseItemViewMap.values.forEach {
            removeView(it)
        }
        courseItemViewMap.clear()
    }

    fun removeCourse(course: CourseItem) {
        val itemView = courseItemViewMap[course]
        courseItemViewMap.remove(course)
        removeView(itemView)
    }

    private fun isDisplayDateText(display: Boolean) {
        val visibility = if (display) View.VISIBLE else View.GONE
        for (i in 0 until 7) {
            val dateTextView = weekLayouts[i].findViewById<TextView>(R.id.id_date_textview)
            if (dateTextView != null) {
                dateTextView.visibility = visibility
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        val availableWidth = (super.getRight() - super.getLeft()).toFloat()
        val availableHeight = (super.getBottom() - super.getTop()).toFloat()

        val totalHeightWeight = dateLayoutHeightWeight + oneDayNodeCount * 1F
        val perCourseItemHeight = 1F / totalHeightWeight * availableHeight
        val firstHorizontalLineY =
            dateLayoutHeightWeight / totalHeightWeight * availableHeight

        val totalWidthWeight = sideLayoutWidthWeight + 7F
        val perCourseItemWidth = 1F / totalWidthWeight * availableWidth
        val firstVerticalLineX =
            sideLayoutWidthWeight / totalWidthWeight * availableWidth

        val linePaint: Paint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 2f
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0F)
        }

        //绘制水平分割线
        if (displayHorizontalLine) {
            var horizontalLineY = firstHorizontalLineY
            for (i in 0 until oneDayNodeCount) {
                canvas.drawLine(
                    0F,
                    horizontalLineY,
                    availableWidth,
                    horizontalLineY,
                    linePaint
                )
                horizontalLineY += perCourseItemHeight
            }
        }

        //绘制垂直分割线
        if (displayVerticalLine) {
            var verticalLineX = firstVerticalLineX
            for (i in 0 until 7) {
                canvas.drawLine(
                    verticalLineX,
                    0F,
                    verticalLineX,
                    availableHeight,
                    linePaint
                )
                verticalLineX += perCourseItemWidth
            }
        }

        canvas.save()
        super.dispatchDraw(canvas)
    }

    private fun getRandomColor(any: Any): Int {
        val id = any.hashCode()
        return colorPool.getOrElse(id) {
            val index = colorPool.size % ColorUtils.lightColorList.size
            val color = ColorUtils.lightColorList[index]
            colorPool[id] = color
            color
        }
    }

    /**
     * 初始化侧边栏
     */
    private fun initNodeLayout() {
        noteLayouts = Array(oneDayNodeCount) { row ->
            RelativeLayout(context).apply {
                layoutParams = LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = spec(row + 1, 1, 1f)
                    columnSpec = spec(0, 1, sideLayoutWidthWeight)
                }
                val nodeTextView = TextView(context).apply {
                    layoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        addRule(RelativeLayout.CENTER_IN_PARENT)
                    }
                    id = R.id.id_node_textview
                    gravity = Gravity.CENTER
                    setTextColor(otherTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
                    paint.isFakeBoldText = true
                    text = (row + 1).toString()
                }
                addView(nodeTextView)
            }
        }
        noteLayouts.forEach { addView(it) }
        updateTimeText()
    }

    /**
     * 初始化左上角Layout
     */
    private fun initCornerLayout() {
        cornerTextView = TextView(context).apply {
            layoutParams = LayoutParams().apply {
                width = 0
                height = 0
                rowSpec = spec(0, 1, dateLayoutHeightWeight)
                columnSpec = spec(0, 1, sideLayoutWidthWeight)
            }
            gravity = Gravity.CENTER
            paint.isFakeBoldText = true
            setTextColor(otherTextColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
        }
        addView(cornerTextView)
    }

    /**
     * 初始化星期日期栏
     */
    private fun initWeekLayout() {
        weekLayouts = Array(7) { column ->
            LinearLayout(context).apply {
                layoutParams = LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = spec(0, 1, dateLayoutHeightWeight)
                    columnSpec = spec(column + 1, 1, 1f)
                }
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                val weekTextView = TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    id = R.id.id_week_textview
                    gravity = Gravity.CENTER
                    text = dayOfWeekCN[column]
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
                    setTextColor(otherTextColor)
                    paint.isFakeBoldText = true
                }
                addView(weekTextView)
            }
        }
        weekLayouts.forEach { addView(it) }
        updateDateText()
    }

    private fun updateDateText() {
        for (i in 0 until dateText.size.coerceAtMost(7)) {
            var dateTextView = weekLayouts[i].findViewById<TextView>(R.id.id_date_textview)
            if (dateTextView == null) {
                dateTextView = TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    id = R.id.id_date_textview
                    gravity = Gravity.CENTER
                    setTextColor(otherTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 11F)
                }
                weekLayouts[i].addView(dateTextView)
            }
            dateTextView.text = dateText[i]
        }
    }

    private fun updateTimeText() {
        for (i in 0 until timeText.size.coerceAtMost(oneDayNodeCount)) {
            var timeTextView = noteLayouts[i].findViewById<TextView>(R.id.id_time_textview)
            if (timeTextView == null) {
                timeTextView = AppCompatTextView(context).apply {
                    layoutParams = RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        addRule(RelativeLayout.CENTER_HORIZONTAL)
                        addRule(RelativeLayout.ALIGN_PARENT_TOP)
                    }
                    id = R.id.id_time_textview
                    gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                    maxLines = 1
                    TextViewCompat.setAutoSizeTextTypeUniformWithPresetSizes(
                        this,
                        intArrayOf(8, 7, 6),
                        TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
                    )
                    setTextColor(otherTextColor)
                }
                noteLayouts[i].addView(timeTextView)
            }
            timeTextView.text = timeText[i]
        }
    }

    private fun updateOtherTextColor() {
        for (noteLayout in noteLayouts) {
            noteLayout.findViewById<TextView>(R.id.id_time_textview)?.setTextColor(otherTextColor)
            noteLayout.findViewById<TextView>(R.id.id_node_textview)?.setTextColor(otherTextColor)
        }
        for (weekLayout in weekLayouts) {
            weekLayout.findViewById<TextView>(R.id.id_week_textview)?.setTextColor(otherTextColor)
            weekLayout.findViewById<TextView>(R.id.id_date_textview)?.setTextColor(otherTextColor)
        }
        cornerTextView.setTextColor(otherTextColor)
    }

    private fun updateMonthText() {
        cornerTextView.text = ("${month}\n月")
    }

    interface OnCourseClickListener {
        fun onClick(course: CourseItem)
    }

    interface OnCourseLongClickListener {
        fun onLongClick(course: CourseItem)
    }
}