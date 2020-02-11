package cn.ifafu.ifafu.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Exam
import cn.ifafu.ifafu.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExamAdapter(
        private val mContext: Context,
        var data: List<Exam> = emptyList()) : RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val format2 = SimpleDateFormat("HH:mm", Locale.CHINA)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.exam_info_recycle_item, parent, false)
        return ExamViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val exam = this.data[position]
        val start = Calendar.getInstance()
        start.time = Date(exam.startTime)
        val weekday = DateUtils.getWeekdayCN(start.get(Calendar.DAY_OF_WEEK))

        holder.tvExamTime.text = if (exam.endTime == 0L) {
            "暂无考试时间"
        } else {
            String.format("%s (%s %s~%s)",
                    format.format(Date(exam.startTime)),
                    weekday,
                    format2.format(Date(exam.startTime)),
                    format2.format(Date(exam.endTime)))
        }

        holder.tvExamName.text = exam.name
        holder.tvExamAddress.text = String.format("%s   %s", exam.address, exam.seatNumber)
        when {
            exam.endTime == 0L -> {
                holder.tvExamLast.text = "未知"
            }
            exam.endTime < System.currentTimeMillis() -> {
                holder.tvExamLast.setText(R.string.exam_over)
            }
            else -> {
                holder.tvExamLast.text = String.format("剩余%s",
                        DateUtils.calcIntervalTime(System.currentTimeMillis(), exam.startTime))
            }
        }
    }

    override fun getItemCount(): Int {
        return this.data.size
    }

    inner class ExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvExamName: TextView = itemView.findViewById(R.id.tv_exam_name)
        var tvExamTime: TextView = itemView.findViewById(R.id.tv_exam_time)
        var tvExamAddress: TextView = itemView.findViewById(R.id.tv_exam_address)
        var tvExamLast: TextView = itemView.findViewById(R.id.tv_exam_last)

    }
}
