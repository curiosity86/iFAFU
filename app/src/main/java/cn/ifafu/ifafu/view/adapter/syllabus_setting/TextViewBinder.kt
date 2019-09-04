package cn.ifafu.ifafu.view.adapter.syllabus_setting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.ifafu.ifafu.R
import me.drakeet.multitype.ItemViewBinder

class TextViewBinder : ItemViewBinder<TextViewItem, TextViewBinder.VH>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        return VH(inflater.inflate(R.layout.item_setting_text, parent, false))
    }

    override fun onBindViewHolder(holder: VH, item: TextViewItem) {
        holder.tvTitle.text = item.title
        holder.tvSubtitle.text = item.subtitle
        holder.itemView.setOnClickListener { item.click.invoke() }
        holder.itemView.setOnLongClickListener {
            item.longClick.invoke()
            true
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tv_subtitle)
    }
}