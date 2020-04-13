package cn.ifafu.ifafu.ui.view.adapter

import androidx.recyclerview.widget.DiffUtil
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.util.toRadiusString
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.item_score_filter.view.*

class ScoreFilterAdapter(private val onCheckedChangeListener: ((score: Score) -> Unit))
    : BaseQuickAdapter<Score, BaseViewHolder>(R.layout.item_score_filter) {

    init {
        setDiffCallback(object : DiffUtil.ItemCallback<Score>() {
            override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
                return oldItem.isIESItem == oldItem.isIESItem
            }
        })
    }

    fun setAllChecked() {
        for (score in data) {
            score.isIESItem = true
        }
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: Score) {
        val titleTV = holder.itemView.tv_score_name
        val scoreTV = holder.itemView.tv_score
        val checkBox = holder.itemView.checkbox
        titleTV.text = item.name
        if (item.realScore == Score.FREE_COURSE) {
            scoreTV.text = "免修";
        } else {
            scoreTV.text = (item.realScore.toRadiusString(2) + "分")
        }
        checkBox.setChecked(item.isIESItem, false)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isIESItem = isChecked
            onCheckedChangeListener.invoke(item)
        }
        holder.itemView.setOnClickListener {
            checkBox.setChecked(!checkBox.isChecked, true)
        }
    }
}
