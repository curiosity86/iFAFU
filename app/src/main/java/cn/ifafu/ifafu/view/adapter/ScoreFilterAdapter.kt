package cn.ifafu.ifafu.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.util.GlobalLib
import cn.ifafu.ifafu.view.custom.SmoothCheckBox

class ScoreFilterAdapter(context: Context, private val onCheckedChangeListener: ((score: Score) -> Unit))
    : RecyclerView.Adapter<ScoreFilterAdapter.ViewHolder>() {

    var data: List<Score> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val layoutInflater = LayoutInflater.from(context)

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
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            score.isIESItem = isChecked
            onCheckedChangeListener.invoke(score)
        }
        holder.itemView.setOnClickListener {
            holder.checkBox.setChecked(!holder.checkBox.isChecked, true)
        }
    }

    fun setAllChecked() {
        for (score in data) {
            score.isIESItem = true
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView = itemView.findViewById(R.id.tv_score_name)
        val scoreTV: TextView = itemView.findViewById(R.id.tv_score_score)
        val checkBox: SmoothCheckBox = itemView.findViewById(R.id.checkbox)
    }
}
