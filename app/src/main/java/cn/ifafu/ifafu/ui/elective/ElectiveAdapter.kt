package cn.ifafu.ifafu.ui.elective

import android.content.Intent
import android.graphics.Color
import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.entity.Score
import cn.ifafu.ifafu.ui.score_item.ScoreItemActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class ElectiveAdapter(data: List<Score>) : BaseQuickAdapter<Score, BaseViewHolder>(R.layout.item_elective, data) {
    override fun convert(helper: BaseViewHolder, item: Score?) {
        if (item == null) return
        helper.setText(R.id.tv_name, item.name)
        if (item.credit == 0F) {
            helper.setText(R.id.tv_credit, "重复")
                    .setTextColor(R.id.tv_credit, Color.GREEN)
        } else if (item.realScore < 60) {
            helper.setText(R.id.tv_credit, "Fail")
                    .setTextColor(R.id.tv_credit, Color.RED)
        } else {
            helper.setText(R.id.tv_credit, item.credit.toString())
                    .setTextColor(R.id.tv_credit, Color.GREEN)
        }
        helper.itemView.setOnClickListener {
            val intent = Intent(mContext, ScoreItemActivity::class.java)
            intent.putExtra("id", item.id)
            mContext.startActivity(intent)
        }
    }

}