package cn.ifafu.ifafu.view.adapter

import cn.ifafu.ifafu.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ScoreItemAdapter(data: Map<String, String>)
    : BaseQuickAdapter<Pair<String, String>, BaseViewHolder>(R.layout.score_item_info_rv_item, data.toList()) {

    override fun convert(helper: BaseViewHolder, item: Pair<String, String>) {
        helper.setText(R.id.tv_title, item.first)
                .setText(R.id.tv_value, item.second)
    }


}