package cn.ifafu.ifafu.view.adapter

import cn.ifafu.ifafu.R
import cn.ifafu.ifafu.entity.CommentItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

class CommentRvAdapter(data: List<CommentItem>, val click: (item: CommentItem) -> Unit) : BaseQuickAdapter<CommentItem, BaseViewHolder>(R.layout.comment_recycle_item, data) {

    override fun convert(helper: BaseViewHolder, item: CommentItem?) {
        helper.run {
            setText(R.id.tv_course_name, item?.courseName)
            setText(R.id.tv_teacher_name, item?.teacherName)
            if (item?.isDone == true) {
                setText(R.id.tv_status, "已评教")
                setTextColor(R.id.tv_status, mContext.resources.getColor(R.color.ifafu_blue))
            } else {
                setText(R.id.tv_status, "未评教")
                setTextColor(R.id.tv_status, mContext.resources.getColor(R.color.red))
            }
            itemView.setOnClickListener {
                if (item != null) {
                    click.invoke(item)
                }
            }
        }
    }
}
