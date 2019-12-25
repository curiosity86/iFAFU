package cn.ifafu.ifafu.mvp.comment

import cn.ifafu.ifafu.base.i.IView
import cn.ifafu.ifafu.base.ifafu.IZFModel
import cn.ifafu.ifafu.base.ifafu.IZFPresenter
import cn.ifafu.ifafu.entity.CommentItem
import cn.ifafu.ifafu.entity.Response
import io.reactivex.Observable

class CommentContract {

    interface Model : IZFModel {
        fun getCommentList(): Observable<Response<List<CommentItem>>>

        fun getJumpInfo(item: CommentItem): Map<String, String>

        fun commentTeacher(item: CommentItem): Observable<Boolean>

        fun submit(params: MutableMap<String, String>): Observable<Boolean>

        fun getSchoolCode(): String
    }

    interface Presenter : IZFPresenter {
        fun click(item: CommentItem)
        fun oneButton()
    }

    interface View : IView {

        fun setRvData(list: List<CommentItem>)

        fun setLoadingText(text: String)

        fun setButtonText(text: String)

        fun showSuccessTip()
    }

}
