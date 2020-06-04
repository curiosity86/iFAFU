package cn.ifafu.ifafu.ui.comment

import cn.ifafu.ifafu.base.mvp.IModel
import cn.ifafu.ifafu.base.mvp.IPresenter
import cn.ifafu.ifafu.base.mvp.IView
import cn.ifafu.ifafu.data.entity.CommentItem
import cn.ifafu.ifafu.data.entity.Response
import io.reactivex.Observable

class CommentContract {

    interface Model: IModel  {
        fun getCommentList(): Observable<Response<MutableList<CommentItem>>>

        fun getJumpInfo(item: CommentItem): Map<String, String>

        fun commentTeacher(item: CommentItem): Observable<Boolean>

        fun submit(params: MutableMap<String, String>): Observable<Boolean>

        fun getSchoolCode(): String
    }

    interface Presenter: IPresenter {
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
