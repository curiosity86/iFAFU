package cn.ifafu.ifafu.view.adapter

import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.data.entity.User
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class AccountAdapter(
        data: List<User>,
        val onClick: (user: User) -> Unit
) : BaseQuickAdapter<User, BaseViewHolder>(R.layout.item_account, data) {

    override fun convert(helper: BaseViewHolder, item: User?) {
        if (item == null) return
        helper.setText(R.id.tv_text, "${item.name}   ${item.account}")
                .setImageResource(R.id.iv_school, when (item.schoolCode) {
                    School.FAFU -> R.drawable.icon_fafu_b_white
                    School.FAFU_JS -> R.drawable.icon_fafu_js_white
                    else -> R.drawable.icon_ifafu_round
                })
        helper.itemView.setOnClickListener {
            this.onClick.invoke(item)
        }
    }

}
