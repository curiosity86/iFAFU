package cn.ifafu.ifafu.ui.view.adapter

import androidx.recyclerview.widget.DiffUtil
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.util.DateUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class ExamAdapter : BaseQuickAdapter<Exam, BaseViewHolder>(R.layout.item_exam_info) {

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val format2 = SimpleDateFormat("HH:mm", Locale.CHINA)

    init {
        setDiffCallback(object : DiffUtil.ItemCallback<Exam>() {
            override fun areItemsTheSame(oldItem: Exam, newItem: Exam): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Exam, newItem: Exam): Boolean {
                return oldItem.id == newItem.id
            }
        })
    }

    override fun convert(holder: BaseViewHolder, item: Exam) {
        val exam = item
        val start = Calendar.getInstance()
        start.time = Date(exam.startTime)
        val weekday = DateUtils.getWeekdayCN(start.get(Calendar.DAY_OF_WEEK))

        holder.setText(R.id.tv_exam_name, exam.name)
        holder.setText(R.id.tv_exam_time, if (exam.endTime == 0L) {
            "暂无考试时间"
        } else {
            String.format("%s (%s %s~%s)",
                    format.format(Date(exam.startTime)),
                    weekday,
                    format2.format(Date(exam.startTime)),
                    format2.format(Date(exam.endTime)))
        })
        holder.setText(R.id.tv_exam_address, String.format("%s   %s", exam.address, exam.seatNumber))
        when {
            exam.endTime == 0L -> {
                holder.setText(R.id.tv_exam_last, "未知")
            }
            exam.endTime < System.currentTimeMillis() -> {
                holder.setText(R.id.tv_exam_last, R.string.exam_over)
            }
            else -> {
                holder.setText(R.id.tv_exam_last, String.format("剩余%s",
                        DateUtils.calcIntervalTime(System.currentTimeMillis(), exam.startTime)))
            }
        }
    }

}
