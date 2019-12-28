package cn.ifafu.ifafu.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.entity.Score
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.view.adapter.ScoreAdapter.ScoreViewHolder

class ScoreAdapter(private val mContext: Context) : RecyclerView.Adapter<ScoreViewHolder>() {

    var scoreList: List<Score> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var mClickListener: ((Score) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.score_list_recycle_item, parent, false)
        return ScoreViewHolder(view)
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val score = scoreList[position]
        holder.tvName.text = score.name
        val calcScore = score.realScore
        if (calcScore == Score.FREE_COURSE) {
            holder.tvScore.text = "免修"
            holder.ivTip.setImageResource(R.drawable.ic_score_mian)
        } else {
            holder.tvScore.text = GlobalLib.formatFloat(calcScore, 2)
            if (calcScore >= 60) {
                holder.tvScore.setTextColor(mContext.resources.getColor(R.color.ifafu_blue))
            } else {
                holder.tvScore.setTextColor(mContext.resources.getColor(R.color.red))
            }
            holder.ivTip.setImageResource(when {
                score.name.contains("体育") -> R.drawable.ic_score_ti
                score.nature.contains("任意选修") -> R.drawable.ic_score_xuan
                calcScore < 60 -> R.drawable.ic_score_warm
                else ->  0
            })
        }
        holder.itemView.setOnClickListener { v: View? ->
            mClickListener?.invoke(score)
        }
    }

    override fun getItemCount(): Int {
        return scoreList.size
    }

    fun setOnScoreClickListener(listener: (Score) -> Unit) {
        mClickListener = listener
    }

    inner class ScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_score_name)
        val tvScore: TextView = itemView.findViewById(R.id.tv_score_score)
        val ivTip: ImageView = itemView.findViewById(R.id.iv_tip)
    }

}