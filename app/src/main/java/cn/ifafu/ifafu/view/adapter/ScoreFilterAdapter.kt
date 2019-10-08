package cn.ifafu.ifafu.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.view.custom.SmoothCheckBox

class ScoreFilterAdapter(context: Context) : RecyclerView.Adapter<ScoreFilterAdapter.ViewHolder>() {

    var data: List<Score> = ArrayList()

    private val layoutInflater = LayoutInflater.from(context)
    private var mOnCheckedListener: OnCheckedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_score_filter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val score = data[position]
        holder.titleTV.text = score.name
        score.realScore.run {
            if (this == Score.FREE_COURSE) {
                holder.scoreTV.text = "免修";
            } else {
                holder.scoreTV.text = GlobalLib.formatFloat(this, 2) + "分";
            }
        }
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setChecked(score.isIESItem, false)
        holder.checkBox.setOnCheckedChangeListener { checkBox, isChecked ->
            Log.d("ScoreFilterAdapter", "OnCheckedChange： $isChecked")
            mOnCheckedListener?.onCheckedChanged(checkBox, score, isChecked)
        }
        holder.itemView.setOnClickListener {
            Log.d("ScoreFilterAdapter", "OnClick: ${!holder.checkBox.isChecked}")
            holder.checkBox.setChecked(!holder.checkBox.isChecked, false)
            mOnCheckedListener?.onCheckedChanged(it, score, holder.checkBox.isChecked)
        }
    }

    fun setOnCheckedListener(listener: OnCheckedListener) {
        mOnCheckedListener = listener
    }

    fun setAllChecked() {
        for (score in data) {
            score.isIESItem = true
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView = itemView.findViewById(R.id.tv_score_name)
        val scoreTV: TextView = itemView.findViewById(R.id.tv_score_score)
        val checkBox: SmoothCheckBox = itemView.findViewById(R.id.checkbox)
    }

    interface OnCheckedListener {
        fun onCheckedChanged(v: View?, item: Score, isChecked: Boolean)
    }
}
