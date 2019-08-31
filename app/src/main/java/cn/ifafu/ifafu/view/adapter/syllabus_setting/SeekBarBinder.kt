package cn.ifafu.ifafu.view.adapter.syllabus_setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import me.drakeet.multitype.ItemViewBinder

class SeekBarBinder(private val onSeekValueChangeListener: (SeekBarItem, Int) -> Unit)
    : ItemViewBinder<SeekBarItem, SeekBarBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_setting_seekbar, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: SeekBarItem) {
        holder.seekBar.max = item.maxValue - item.minValue
        holder.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onSeekValueChangeListener.invoke(item, progress + item.minValue)
                holder.tvSubtitle.text = ("${item.minValue + progress} ${item.unit}")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        holder.tvTitle.text = item.title
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle : TextView = itemView.findViewById(R.id.tv_title)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tv_subtitle)
        val seekBar: SeekBar = itemView.findViewById(R.id.seekBar)
    }
}