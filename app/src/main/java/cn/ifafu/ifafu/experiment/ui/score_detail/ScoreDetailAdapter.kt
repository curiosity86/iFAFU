package cn.ifafu.ifafu.experiment.ui.score_detail

import cn.ifafu.ifafu.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ScoreDetailAdapter
    : BaseQuickAdapter<Pair<String, String>, BaseViewHolder>(R.layout.item_score_detail) {

    override fun convert(holder: BaseViewHolder, item: Pair<String, String>) {
        holder.setText(R.id.tv_title, item.first)
                .setText(R.id.tv_value, item.second)
    }


}