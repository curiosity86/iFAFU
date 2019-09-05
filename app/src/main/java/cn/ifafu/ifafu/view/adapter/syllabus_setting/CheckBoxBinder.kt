package cn.ifafu.ifafu.view.adapter.syllabus_setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.view.custom.SmoothCheckBox
import me.drakeet.multitype.ItemViewBinder

class CheckBoxBinder
    : ItemViewBinder<CheckBoxItem, CheckBoxBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_setting_checkbox, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CheckBoxItem) {
        holder.tvTitle.text = item.title
        holder.checkBox.setChecked(item.checked, false)
        holder.itemView.setOnClickListener {
            holder.checkBox.isChecked = !holder.checkBox.isChecked
            holder.checkBox.setChecked(holder.checkBox.isChecked, true)
            item.listener.invoke(holder.checkBox.isChecked)
        }
        holder.checkBox.setOnCheckedChangeListener { checkBox, isChecked ->
            item.listener.invoke(isChecked)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle : TextView = itemView.findViewById(R.id.tv_title)
        val checkBox: SmoothCheckBox = itemView.findViewById(R.id.checkbox)
    }
}