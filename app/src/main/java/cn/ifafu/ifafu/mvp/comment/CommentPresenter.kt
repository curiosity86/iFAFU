package cn.ifafu.ifafu.mvp.comment

import android.content.Intent
import cn.ifafu.ifafu.app.School
import cn.ifafu.ifafu.base.BasePresenter
import cn.ifafu.ifafu.base.addDisposable
import cn.ifafu.ifafu.entity.CommentItem
import cn.ifafu.ifafu.entity.Response
import cn.ifafu.ifafu.mvp.web.WebActivity
import cn.ifafu.ifafu.util.RxUtils
import io.reactivex.rxkotlin.toObservable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CommentPresenter(view: CommentContract.View) : BasePresenter<CommentContract.View, CommentContract.Model>(view, CommentModel(view.context)), CommentContract.Presenter {

    private var response: Response<List<CommentItem>>? = null

    private var flag = false //是否评测完

    private var first = true

    override fun onCreate() {
        super.onCreate()
        init()
    }

    override fun click(item: CommentItem) {
        GlobalScope.launch(Dispatchers.IO) {
            if (mModel.getSchoolCode() != School.FAFU_JS) {
                val intent = Intent(mView.context, WebActivity::class.java)
                mModel.getJumpInfo(item).forEach { (k, u) ->
                    intent.putExtra(k, u)
                }
                launch(Dispatchers.Main) {
                    mView.openActivity(intent)
                }
            }
        }
    }

    override fun oneButton() {
        when {
            response == null -> return
            flag -> { //所有老师都评测完，提交评教
                addDisposable {
                    mModel.submit(response!!.hiddenParams)
                            .compose(RxUtils.ioToMain())
                            .compose(showHideLoading())
                            .subscribe({
                                if (it) {
                                    mView.showMessage("提交评教成功")
                                    mView.killSelf()
                                } else {
                                    mView.showMessage("提交评教失败，请前往网页模式提交评教~")
                                }
                            }, this::onError)
                }
                return
            }
            else -> { //一键评教
                mView.setLoadingText("一键评教中")
                addDisposable {
                    response!!.body.toObservable()
                            .flatMap { mModel.commentTeacher(it) }
                            .compose(RxUtils.ioToMain())
                            .doOnSubscribe { mView.showLoading() }
                            .doFinally {
                                init()
                                mView.hideLoading()
                            }
                            .subscribe({
                            }, this::onError)
                }
            }
        }
    }

    private fun init() {
        addDisposable {
            mModel.getCommentList()
                    .compose(RxUtils.ioToMain())
                    .compose(showHideLoading())
                    .subscribe({
                        if (it.code == Response.FAILURE) {
                            mView.showMessage(it.message)
                            mView.killSelf()
                        } else {
                            response = it
                            mView.setRvData(it.body)
                            //检查是否所有老师都评教完
                            flag = true
                            for (item in it.body) {
                                if (!item.isDone) {
                                    flag = false
                                    break
                                }
                            }
                            if (flag) {
                                if (!first) {
                                    mView.showMessage("一键评教完成")
                                    mView.showSuccessTip()
                                } else {
                                    first = false
                                }
                                mView.setButtonText("提交最终评教")
                            }
                        }
                    }, {
                        super.onError(it)
                        if (it.message?.contains("评教") == true) {
                            mView.killSelf()
                        }
                    })
        }
    }
}
