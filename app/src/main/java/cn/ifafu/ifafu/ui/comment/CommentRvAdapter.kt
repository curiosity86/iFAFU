package cn.ifafu.ifafu.ui.comment

import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.data.bean.CommentItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

@Suppress("DEPRECATION")
class CommentRvAdapter(data: List<CommentItem>, val click: (item: CommentItem) -> Unit)
    : BaseQuickAdapter<CommentItem, BaseViewHolder>(R.layout.item_comment, data.toMutableList()) {

    override fun convert(holder: BaseViewHolder, item: CommentItem) {
        holder.run {
            setText(R.id.tv_course_name, item.courseName)
            setText(R.id.tv_teacher_name, item.teacherName)
            if (item.isDone) {
                setText(R.id.tv_status, "已评教")
                setTextColor(R.id.tv_status, context.resources.getColor(R.color.ifafu_blue))
            } else {
                setText(R.id.tv_status, "未评教")
                setTextColor(R.id.tv_status, context.resources.getColor(R.color.red))
            }
            itemView.setOnClickListener {
                click.invoke(item)
            }
        }
    }
}
