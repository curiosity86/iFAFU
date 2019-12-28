package cn.ifafu.ifafu.view.adapter

import android.graphics.Color
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.entity.Score
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ElectiveAdapter(data: List<Score>, var onClick: (Score) -> Unit) : BaseQuickAdapter<Score, BaseViewHolder>(R.layout.elective_recycle_item, data) {
    override fun convert(helper: BaseViewHolder, item: Score?) {
        if (item == null) return
        helper.setText(R.id.tv_name, item.name)
        if (item.realScore < 60) {
            helper.setText(R.id.tv_credit, "Fail")
                    .setTextColor(R.id.tv_credit, Color.RED)
        } else {
            helper.setText(R.id.tv_credit, item.credit.toString())
                    .setTextColor(R.id.tv_credit, Color.GREEN)
        }
        helper.itemView.setOnClickListener {
            onClick(item)
        }
    }
}