package cn.ifafu.ifafu.repository

import cn.ifafu.ifafu.data.bean.CommentItem
import cn.ifafu.ifafu.data.entity.User
import cn.ifafu.ifafu.data.new_http.IFHttpClient
import cn.ifafu.ifafu.experiment.bean.IFResponse
import cn.ifafu.ifafu.util.encode

class CommentServiceFAFU: CommentService {
    private val http = IFHttpClient()

    override fun getCommentList(user: User): IFResponse<List<CommentItem>> {
        val baseUrl = "http://jwgl.fafu.edu.cn/${user.token}"
        val commentUrl = "${baseUrl}/xsjxpj2fafu2.aspx?xh=${user.account}&xm=${user.name.encode()}&gnmkdm=N121400"
        val params =
    }

    private fun getParams(url: String, header: Map<String, String>): Map<String, String> {
        http.get()
    }
}