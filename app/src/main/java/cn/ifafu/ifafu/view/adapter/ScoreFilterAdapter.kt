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

class ScoreFilterAdapter(context: Context, var data: List<Score>) : RecyclerView.Adapter<ScoreFilterAdapter.ViewHolder>() {

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
        holder.scoreTV.text =  GlobalLib.trimZero(String.format("%.2f", score.score)) + "分"
        holder.checkBox.setChecked(score.isIESItem, false)
        holder.checkBox.setOnCheckedChangeListener { checkBox, isChecked ->
            mOnCheckedListener?.onCheckedChanged(checkBox, score, isChecked)
        }
        holder.itemView.setOnClickListener {
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