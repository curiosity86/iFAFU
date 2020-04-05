package cn.ifafu.ifafu.experiment.ui.elective

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.util.ColorUtils


class ElectiveView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mClickListener: ((View, Score) -> Unit)? = null
    private var rootLayout: LinearLayout

    private var elective: Elective? = null

    private var isCollapse = true

    private var btnSign: ImageButton
    private var tvCategory: TextView
    private var tvStatistics: TextView
    private var tvEmpty: TextView

    private var itemViewMap = HashMap<Score, View>()

    //ShowMore按钮动画
    private val expandAnimation by lazy { getRotateAnimation(0F, -180F) }
    private val collapseAnimation by lazy { getRotateAnimation(-180F, 0F) }

    init {
        val view = LayoutInflater.from(getContext()).inflate(R.layout.view_elective_item, this)

        rootLayout = view.findViewById(R.id.layout_root)
        btnSign = view.findViewById(R.id.btn_sign)
        rootLayout.setOnClickListener {
            expandOrCollapse()
        }

        tvCategory = view.findViewById(R.id.category)
        tvStatistics = view.findViewById(R.id.statistics)

        tvEmpty = view.findViewById(R.id.tv_empty)

    }

    fun setElective(elective: Elective?) {
        if (elective == null) return
        itemViewMap.clear()
        for (view in itemViewMap.values) {
            rootLayout.removeView(view)
        }
        this.elective = elective
        tvCategory.text = elective.category
        tvStatistics.text = elective.statistics
        if (elective.done) {
            tvStatistics.setTextColor(ColorUtils.getColor(context, R.color.green))
        } else {
            tvStatistics.setTextColor(ColorUtils.getColor(context, R.color.red_2))
        }
        for (score in elective.scores) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_elective, rootLayout, false).apply {
                findViewById<TextView>(R.id.tv_name).text = score.name
                val scoreTv = findViewById<TextView>(R.id.tv_credit)
                when {
                    score.credit == 0F -> {
                        scoreTv.text = "重复"
                        scoreTv.setTextColor(Color.GREEN)
                    }
                    score.realScore < 60 -> {
                        scoreTv.text = ("Fail")
                        scoreTv.setTextColor(Color.RED)
                    }
                    else -> {
                        scoreTv.text = score.credit.toString()
                        scoreTv.setTextColor(Color.GREEN)
                    }
                }
            }
            view.visibility = View.GONE
            itemViewMap[score] = view
            mClickListener?.let { listener ->
                view.setOnClickListener { v: View? ->
                    v?.let { listener.invoke(it, score) }
                }
            }
            rootLayout.addView(view)
        }
    }

    fun getElective(): Elective? {
        return elective
    }

    fun setOnScoreClickListener(listener: (View, Score) -> Unit) {
        mClickListener = listener
        itemViewMap.entries.forEach { e ->
            mClickListener?.let { listener ->
                e.value.setOnClickListener { v: View? ->
                    v?.let { listener.invoke(it, e.key) }
                }
            }
        }
    }

    private fun expandOrCollapse() {
        if (isCollapse) {
            isCollapse = false
            btnSign.startAnimation(expandAnimation)
            if (itemViewMap.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                itemViewMap.values.forEach {
                    it.visibility = View.VISIBLE
                }
            }
        } else {
            isCollapse = true
            btnSign.startAnimation(collapseAnimation)
            if (itemViewMap.isEmpty()) {
                tvEmpty.visibility = View.GONE
            } else {
                itemViewMap.values.forEach {
                    it.visibility = View.GONE
                }
            }
        }
    }

    private fun getRotateAnimation(fromDegrees: Float, toDegrees: Float): RotateAnimation {
        val animation = RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        animation.fillAfter = true
        animation.interpolator = LinearInterpolator()
        animation.duration = 300L
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                rootLayout.isClickable = true
            }

            override fun onAnimationStart(animation: Animation?) {
                rootLayout.isClickable = false
            }
        })
        return animation
    }

}

