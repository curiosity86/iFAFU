package cn.ifafu.ifafu.ui.elective

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.util.ColorUtils
import com.nineoldandroids.animation.ValueAnimator


class ElectiveView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var view: View

    private var elective: Elective? = null

    private var isCollapse = true

    private var btnSign: ImageButton
    private var tvCategory: TextView
    private var tvStatistics: TextView
    private var rvMore: RecyclerView
    private var tvEmpty: TextView

    private val expandAnimation by lazy { getRotateAnimation(0F, -180F) }
    private val collapseAnimation by lazy { getRotateAnimation(-180F, 0F) }

    init {
        val inflater = LayoutInflater.from(getContext())
        view = inflater.inflate(R.layout.view_elective_item, this)
        btnSign = view.findViewById(R.id.btn_sign)
        tvCategory = view.findViewById(R.id.category)
        tvStatistics = view.findViewById(R.id.statistics)

        rvMore = view.findViewById(R.id.recyclerview)
        rvMore.layoutManager = LinearLayoutManager(context)
        rvMore.visibility = View.GONE
        tvEmpty = view.findViewById(R.id.tv_empty)

        view.setOnClickListener {
            if (isCollapse) expand() else collapse()
        }
    }

    fun setElective(elective: Elective?) {
        if (elective == null) return
        this.elective = elective
        tvCategory.text = elective.category
        tvStatistics.text = elective.statistics
        if (elective.done) {
            tvStatistics.setTextColor(ColorUtils.getColor(context, R.color.green))
        } else {
            tvStatistics.setTextColor(ColorUtils.getColor(context, R.color.red_2))
        }
        rvMore.adapter = ElectiveAdapter(elective.scores)
    }

    fun getElective(): Elective? {
        return elective
    }

    private fun expand() {
        isCollapse = false
        btnSign.startAnimation(expandAnimation)
        if (elective?.scores.isNullOrEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            rvMore.visibility = View.VISIBLE
        }
    }

    private fun collapse() {
        isCollapse = true
        btnSign.startAnimation(collapseAnimation)
        rvMore.visibility = View.GONE
        tvEmpty.visibility = View.GONE
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
                view.isClickable = true
            }

            override fun onAnimationStart(animation: Animation?) {
                view.isClickable = false
            }
        })
        return animation
    }
    private fun showHideTitle(view: View, maxHeight: Int, duration: Long, isShow: Boolean) {
        val animator = if (isShow) {
            ValueAnimator.ofFloat(0f, 1f)
        } else {
            ValueAnimator.ofFloat(1f, 0f)
        }
        animator.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            val params = view.layoutParams
            params.height = (currentValue * maxHeight).toInt()
            view.layoutParams = params
        }
        animator.setDuration(duration).start()
    }
}

