package cn.ifafu.ifafu.experiment.ui.score_list

import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.util.ColorUtils
import cn.ifafu.ifafu.util.toRadiusString
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ScoreListAdapter : BaseQuickAdapter<Score, BaseViewHolder>(R.layout.item_score_list) {

    init {
        setDiffCallback(object : DiffUtil.ItemCallback<Score>() {
            override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
                return oldItem.name == newItem.name && oldItem.score == newItem.score
            }

        })
    }

    override fun convert(holder: BaseViewHolder, item: Score) {
        holder.setText(R.id.tv_score_name, item.name)
        val calcScore = item.realScore
        if (calcScore == Score.FREE_COURSE) {
            holder.setText(R.id.tv_score, "免修")
        } else {
            holder.setText(R.id.tv_score, calcScore.toRadiusString(2))
        }
        if (calcScore >= 60 || calcScore == Score.FREE_COURSE) {
            holder.setTextColor(R.id.tv_score, ColorUtils.getColor(context, R.color.ifafu_blue))
        } else {
            holder.setTextColor(R.id.tv_score, ColorUtils.getColor(context, R.color.red))
        }
        when {
            calcScore == Score.FREE_COURSE ->
                holder.setImageResource(R.id.iv_tip, R.drawable.ic_score_mian)
            item.name.contains("体育") ->
                holder.setImageResource(R.id.iv_tip, R.drawable.ic_score_ti)
            item.nature.contains("任意选修") || item.nature.contains("公共选修") ->
                holder.setImageResource(R.id.iv_tip, R.drawable.ic_score_xuan)
            calcScore < 60 ->
                holder.setImageResource(R.id.iv_tip, R.drawable.ic_score_warm)
            else ->
                holder.setImageDrawable(R.id.iv_tip, null)
        }
        holder.itemView.setOnClickListener { v ->
            val extras = FragmentNavigatorExtras(
                    holder.itemView to context.getString(R.string.transition_score_name)
            )
            val action = ScoreListFragmentDirections
                    .actionFragmentScoreListToFragmentScoreDetail(item.id)
            v.findNavController().navigate(action)
        }
    }
}