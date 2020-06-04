package cn.ifafu.ifafu.repository

import cn.ifafu.ifafu.data.bean.CommentItem
import cn.ifafu.ifafu.experiment.bean.IFResponse

interface CommentService {

    fun getCommentList(): IFResponse<List<CommentItem>>

}